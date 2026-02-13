package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserValidationPort;
import com.pragma.plazoleta.infrastructure.constant.SecurityConstants;
import com.pragma.plazoleta.infrastructure.constant.ValidationConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPort userValidationPort;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        validateRestaurantData(restaurant);
        return restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public PagedResult<Restaurant> getAllRestaurants(int page, int size) {
        return restaurantPersistencePort.findAllOrderedByNamePaginated(page, size);
    }

    private void validateRestaurantData(Restaurant restaurant) {
        validateName(restaurant.getName());
        validateNit(restaurant.getNit());
        validatePhone(restaurant.getPhone());
        validateOwner(restaurant.getOwnerId());
        validateRestaurantDoesNotExist(restaurant.getNit());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRestaurantNameException("Restaurant name is required");
        }

        if (ValidationConstants.NAME_ONLY_NUMBERS_PATTERN.matcher(name.trim()).matches()) {
            throw new InvalidRestaurantNameException(
                    "Restaurant name can contain numbers but cannot consist of only numbers"
            );
        }
    }

    private void validateNit(String nit) {
        if (nit == null || nit.isBlank()) {
            throw new InvalidNitException("NIT is required");
        }

        if (!ValidationConstants.NIT_PATTERN.matcher(nit).matches()) {
            throw new InvalidNitException("NIT must be numeric only");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Phone is required");
        }

        if (phone.length() > ValidationConstants.MAX_PHONE_LENGTH) {
            throw new InvalidPhoneException(
                    "Phone must have a maximum of 13 characters"
            );
        }

        if (!ValidationConstants.PHONE_PATTERN.matcher(phone).matches()) {
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

        if (!SecurityConstants.ROLE_OWNER.equals(role)) {
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
