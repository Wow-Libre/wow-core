package com.register.wowlibre.domain.constant;

public class Constants {
    // CONSTANT
    public static final String CONSTANT_UNIQUE_ID = "uniqueID";
    public static final String CONSTANT_ROL = "x-roles";
    // HEADERS
    public static final String HEADER_TRANSACTION_ID = "transaction_id";
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEADER_IP_ADDRESS = "X-Forwarded-For";
    public static final String HEADER_CLIENT = "X-Client";

    /* JWT HEADERS */
    public static final String HEADER_EMAIL = "x-email";
    public static final String HEADER_USER_ID = "x-user-id";

    public static final String HEADER_LANGUAGE = "x-language";

    /**
     * PARAMS CUSTOM
     **/
    public static final String PARAM_PROVIDER_ID = "provider_id";
    public static final String PARAM_ACCOUNT_ID = "account_id";
    public static final String PARAM_SERVER_ID = "server_id";
    public static final String PARAM_CHARACTER_ID = "character_id";

    public static final String PATH_VARIABLE_FRIEND_ID = "friend_id";


    public static class Errors {
        public static final String CONSTANT_GENERIC_ERROR_MESSAGE = "An unexpected error has occurred and it was not " +
                "possible to authenticate to the system, please try again later";
      
    }
}
