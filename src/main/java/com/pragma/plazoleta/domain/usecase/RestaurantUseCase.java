package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserValidationPort;

import java.util.regex.Pattern;

public class RestaurantUseCase implements IRestaurantServicePort {

    private static final String ROLE_OWNER = "OWNER";
    private static final int MAX_PHONE_LENGTH = 13;

    // Name can contain letters, numbers and spaces, but not ONLY numbers
    private static final Pattern NAME_ONLY_NUMBERS_PATTERN = Pattern.compile("^\\d+$");

    // NIT must be numeric only
    private static final Pattern NIT_PATTERN = Pattern.compile("^\\d+$");

    // Phone: max 13 chars, can contain + at start, rest must be digits
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{1,12}$");

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPort userValidationPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                             IUserValidationPort userValidationPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userValidationPort = userValidationPort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        validateName(restaurant.getName());
        validateNit(restaurant.getNit());
        validatePhone(restaurant.getPhone());
        validateOwner(restaurant.getOwnerId());
        validateRestaurantDoesNotExist(restaurant.getNit());

        return restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public PagedResult<Restaurant> getAllRestaurants(int page, int size) {
        return restaurantPersistencePort.findAllOrderedByNamePaginated(page, size);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRestaurantNameException("Restaurant name is required");
        }

        if (NAME_ONLY_NUMBERS_PATTERN.matcher(name.trim()).matches()) {
            throw new InvalidRestaurantNameException(
                    "Restaurant name can contain numbers but cannot consist of only numbers"
            );
        }
    }

    private void validateNit(String nit) {
        if (nit == null || nit.isBlank()) {
            throw new InvalidNitException("NIT is required");
        }

        if (!NIT_PATTERN.matcher(nit).matches()) {
            throw new InvalidNitException("NIT must be numeric only");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Phone is required");
        }

        if (phone.length() > MAX_PHONE_LENGTH) {
            throw new InvalidPhoneException(
                    "Phone must have a maximum of 13 characters"
            );
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneException(
                    "Phone must be numeric and may contain the + symbol at the start. Example: +573005698325"
            );
        }
    }

    private void validateOwner(Long ownerId) {
        if (ownerId == null) {
            throw new OwnerNotFoundException("Owner ID is required");
        }

        String role = userValidationPort.getUserRoleById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(
                        "User with ID " + ownerId + " does not exist"
                ));

        if (!ROLE_OWNER.equals(role)) {
            throw new UserNotOwnerException(
                    "User with ID " + ownerId + " does not have the OWNER role"
            );
        }
    }

    private void validateRestaurantDoesNotExist(String nit) {
        if (restaurantPersistencePort.existsByNit(nit)) {
            throw new RestaurantAlreadyExistsException(
                    "A restaurant already exists with NIT: " + nit
            );
        }
    }
}
