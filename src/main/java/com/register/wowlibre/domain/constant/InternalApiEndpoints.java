package com.register.wowlibre.domain.constant;

import java.util.*;

public class InternalApiEndpoints {

    // Account endpoints
    private static final String[] ACCOUNT_ENDPOINTS = {
            "/api/account/create",
            "/api/account/password-recovery/request",
            "/api/account/password-recovery/confirm",
            "/api/account/search"
    };

    // Resources endpoints
    private static final String[] RESOURCES_ENDPOINTS = {
            "/api/resources/banners-home",
            "/api/resources/country",
            "/api/resources/widget-home",
            "/api/resources/faqs",
            "/api/resources/bank/plans",
            "/api/resources/benefit",
            "/api/resources/benefits-guild",
            "/api/resources/plan-acquisition"
    };

    // Realm endpoints
    private static final String[] REALM_ENDPOINTS = {
            "/api/realm/advertising/language",
            "/api/realm",
            "/api/realm/vdp"
    };

    // Guilds endpoints
    private static final String[] GUILDS_ENDPOINTS = {
            "/api/guilds",
            "/api/guilds/{id}"
    };

    // News endpoints
    private static final String[] NEWS_ENDPOINTS = {
            "/api/news",
            "/api/news/{id}"
    };

    // Transaction endpoints
    private static final String[] TRANSACTION_ENDPOINTS = {
            "/api/transaction/purchase"
    };

    // Banners endpoints
    private static final String[] BANNERS_ENDPOINTS = {
            "/api/banners",
            "/actuator/**"
    };

    /**
     * Combines all internal API endpoints into a single array.
     * This array contains all endpoints that should be publicly accessible without authentication.
     *
     * @return Array of all internal API endpoints
     */
    public static String[] getAllInternalApiEndpoints() {
        List<String> allEndpoints = new ArrayList<>();

        allEndpoints.addAll(List.of(ACCOUNT_ENDPOINTS));
        allEndpoints.addAll(List.of(RESOURCES_ENDPOINTS));
        allEndpoints.addAll(List.of(REALM_ENDPOINTS));
        allEndpoints.addAll(List.of(GUILDS_ENDPOINTS));
        allEndpoints.addAll(List.of(NEWS_ENDPOINTS));
        allEndpoints.addAll(List.of(TRANSACTION_ENDPOINTS));
        allEndpoints.addAll(List.of(BANNERS_ENDPOINTS));

        return allEndpoints.toArray(new String[0]);
    }

    /**
     * Get account-related endpoints
     */
    public static String[] getAccountEndpoints() {
        return ACCOUNT_ENDPOINTS;
    }

    /**
     * Get resources-related endpoints
     */
    public static String[] getResourcesEndpoints() {
        return RESOURCES_ENDPOINTS;
    }

    /**
     * Get realm-related endpoints
     */
    public static String[] getRealmEndpoints() {
        return REALM_ENDPOINTS;
    }

    /**
     * Get guilds-related endpoints
     */
    public static String[] getGuildsEndpoints() {
        return GUILDS_ENDPOINTS;
    }

    /**
     * Get news-related endpoints
     */
    public static String[] getNewsEndpoints() {
        return NEWS_ENDPOINTS;
    }

    /**
     * Get transaction-related endpoints
     */
    public static String[] getTransactionEndpoints() {
        return TRANSACTION_ENDPOINTS;
    }

    /**
     * Get banners-related endpoints
     */
    public static String[] getBannersEndpoints() {
        return BANNERS_ENDPOINTS;
    }
}

