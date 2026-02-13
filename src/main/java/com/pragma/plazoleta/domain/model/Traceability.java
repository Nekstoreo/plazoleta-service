package com.pragma.plazoleta.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<TraceabilityOrderItem> orderItems = new ArrayList<>();
    private Long totalOrderAmount;

    public Traceability() {
                /* Empty constructor intentionally left.
           Required by persistence/serialization frameworks and libraries
           (e.g., Jackson, JPA, MapStruct) that instantiate the class via reflection.
           Should not contain additional logic. */
    }

}
