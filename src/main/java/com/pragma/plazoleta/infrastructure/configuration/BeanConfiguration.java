package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IEfficiencyServicePort;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.spi.*;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import com.pragma.plazoleta.domain.usecase.EfficiencyUseCase;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IRestaurantServicePort restaurantServicePort(
            IRestaurantPersistencePort restaurantPersistencePort,
            IUserValidationPort userValidationPort) {
        return new RestaurantUseCase(restaurantPersistencePort, userValidationPort);
    }

    @Bean
    public IDishServicePort dishServicePort(
            IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort) {
        return new DishUseCase(dishPersistencePort, restaurantPersistencePort);
    }

    @Bean
    public IOrderServicePort orderServicePort(
            IOrderPersistencePort orderPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IDishPersistencePort dishPersistencePort,
            IEmployeeRestaurantPort employeeRestaurantPort,
            IClientInfoPort clientInfoPort,
            INotificationPort notificationPort,
            ITraceabilityPort traceabilityPort) {
        return new OrderUseCase(
                orderPersistencePort,
                restaurantPersistencePort,
                dishPersistencePort,
                employeeRestaurantPort,
                clientInfoPort,
                notificationPort,
                traceabilityPort
        );
    }

    @Bean
    public IEfficiencyServicePort efficiencyServicePort(
            ITraceabilityPort traceabilityPort,
            IRestaurantPersistencePort restaurantPersistencePort) {
        return new EfficiencyUseCase(traceabilityPort, restaurantPersistencePort);
    }
}
