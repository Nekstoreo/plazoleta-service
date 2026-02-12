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
                /* Empty constructor intentionally left.
           Required by persistence/serialization frameworks and libraries
           (e.g., Jackson, JPA, MapStruct) that instantiate the class via reflection.
           Should not contain additional logic. */
    }

}
