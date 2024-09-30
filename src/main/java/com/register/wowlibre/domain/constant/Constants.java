package com.register.wowlibre.domain.constant;

public class Constants {
    // CONSTANT
    public static final String CONSTANT_UNIQUE_ID = "uniqueID";
    public static final String CONSTANT_ROL_JWT_PROP = "roles";
    // HEADERS
    public static final String HEADER_TRANSACTION_ID = "transaction_id";

    /* JWT HEADERS */
    public static final String HEADER_EMAIL_JWT = "x-email";
    public static final String HEADER_ACCOUNT_WEB_ID_JWT = "x-account_web_id";
    public static final String HEADER_LANGUAGE_JWT = "x-language";

    public static class Errors {
        public static final String CONSTANT_GENERIC_ERROR_MESSAGE = "An unexpected error has occurred and it was not " +
                "possible to authenticate to the system, please try again later";
        public static final String CONSTANT_GENERIC_ERROR_ACCOUNT_IS_NOT_AVAILABLE = "The account is not available or does not exist ";
    }
}
