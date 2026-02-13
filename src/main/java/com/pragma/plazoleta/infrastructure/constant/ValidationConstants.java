package com.pragma.plazoleta.infrastructure.constant;

import java.util.regex.Pattern;

public final class ValidationConstants {

    private ValidationConstants() {
        throw new AssertionError("Cannot instantiate ValidationConstants");
    }

    public static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?\\d{1,12}$"
    );

    public static final Pattern NIT_PATTERN = Pattern.compile(
            "^\\d+$"
    );

    public static final Pattern NAME_ONLY_NUMBERS_PATTERN = Pattern.compile(
            "^\\d+$"
    );

    public static final int MAX_PHONE_LENGTH = 13;
    public static final int MIN_ITEMS_PER_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MIN_PRICE = 0;
    public static final int MAX_RESTAURANT_NAME_LENGTH = 100;
    public static final int MAX_DISH_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
}
