package com.pragma.plazoleta.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Traceability {
    private String id;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private LocalDateTime date;
    private String previousStatus;
    private String newStatus;
    private Long employeeId;
    private String employeeEmail;
    private Long restaurantId;

    public Traceability() {
                /* Constructor vacío intencional.
           Requerido por frameworks y bibliotecas de persistencia/serialización
           (por ejemplo, Jackson, JPA, MapStruct) que instancian la clase mediante reflexión.
           No debe contener lógica adicional. */
    }

}
