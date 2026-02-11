package com.register.wowlibre.domain.port.in.web_users;

import com.register.wowlibre.domain.dto.user.WebUsersPageDto;

public interface WebUsersDashboardPort {
    WebUsersPageDto getWebUsersPage(String emailFilter, int page, int size, String transactionId);
}
