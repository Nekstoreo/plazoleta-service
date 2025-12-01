package com.pragma.plazoleta.domain.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Restaurant {

    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String logoUrl;
    private Long ownerId;

    public Restaurant() {
    }

    public Restaurant(String name, String nit, String address, String phone, String logoUrl, Long ownerId) {
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.phone = phone;
        this.logoUrl = logoUrl;
        this.ownerId = ownerId;
    }

}
