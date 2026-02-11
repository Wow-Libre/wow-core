package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.user.WebUsersPageDto;
import com.register.wowlibre.domain.port.in.web_users.WebUsersDashboardPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;

@RestController
@RequestMapping("/api/users/admin")
public class WebUsersDashboardController {

    private final WebUsersDashboardPort webUsersDashboardPort;

    public WebUsersDashboardController(WebUsersDashboardPort webUsersDashboardPort) {
        this.webUsersDashboardPort = webUsersDashboardPort;
    }

    @GetMapping("/web")
    public ResponseEntity<GenericResponse<WebUsersPageDto>> getWebUsersPage(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String email) {

        WebUsersPageDto result = webUsersDashboardPort.getWebUsersPage(
                email != null ? email.trim() : null,
                page,
                size,
                transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(result, transactionId).ok().build());
    }
}
