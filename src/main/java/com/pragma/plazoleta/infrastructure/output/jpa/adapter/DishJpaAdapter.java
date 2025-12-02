package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final DishEntityMapper dishEntityMapper;

    public DishJpaAdapter(IDishRepository dishRepository, DishEntityMapper dishEntityMapper) {
        this.dishRepository = dishRepository;
        this.dishEntityMapper = dishEntityMapper;
    }

    @Override
    public Dish saveDish(Dish dish) {
        DishEntity entity = dishEntityMapper.toEntity(dish);
        DishEntity savedEntity = dishRepository.save(entity);
        return dishEntityMapper.toDish(savedEntity);
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id)
                .map(dishEntityMapper::toDish);
    }

    @Override
    public PagedResult<Dish> findActiveDishesByRestaurantId(Long restaurantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DishEntity> dishPage = dishRepository.findByRestaurantIdAndActiveTrue(restaurantId, pageable);
        
        return toPagedResult(dishPage);
    }

    @Override
    public PagedResult<Dish> findActiveDishesByRestaurantIdAndCategory(
            Long restaurantId, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DishEntity> dishPage = dishRepository.findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
                restaurantId, category, pageable);
        
        return toPagedResult(dishPage);
    }

    private PagedResult<Dish> toPagedResult(Page<DishEntity> dishPage) {
        List<Dish> dishes = dishPage.getContent().stream()
                .map(dishEntityMapper::toDish)
                .toList();
        
        return PagedResult.of(
                dishes,
                dishPage.getNumber(),
                dishPage.getSize(),
                dishPage.getTotalElements(),
                dishPage.getTotalPages()
        );
    }
}
