package com.pragma.plazoleta.infrastructure.constant;

public final class ApiConstants {

    private ApiConstants() {
        throw new AssertionError("Cannot instantiate ApiConstants");
    }

    public static final String API_VERSION = "/api/v1";

    public static final String RESTAURANTS_BASE_PATH = API_VERSION + "/restaurants";
    public static final String DISHES_BASE_PATH = API_VERSION + "/dishes";
    public static final String ORDERS_BASE_PATH = API_VERSION + "/orders";
    public static final String EFFICIENCY_BASE_PATH = API_VERSION + "/efficiency";

    public static final String DISHES_SUFFIX = "/dishes";
    public static final String CATEGORIES_SUFFIX = "/categories";

    public static final String SWAGGER_PATH = "/swagger-ui.html";
    public static final String API_DOCS_PATH = "/api-docs";

    public static final String STATUS_200 = "200";
    public static final String STATUS_201 = "201";
    public static final String STATUS_400 = "400";
    public static final String STATUS_401 = "401";
    public static final String STATUS_403 = "403";
    public static final String STATUS_404 = "404";
    public static final String STATUS_409 = "409";
    public static final String STATUS_500 = "500";

    public static final String APPLICATION_JSON = "application/json";

    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_IN_PREPARATION = "IN_PREPARATION";
    public static final String ORDER_STATUS_READY = "READY";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
}
