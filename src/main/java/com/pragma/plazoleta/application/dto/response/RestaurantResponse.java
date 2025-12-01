package com.pragma.plazoleta.application.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String logoUrl;
    private Long ownerId;
}
