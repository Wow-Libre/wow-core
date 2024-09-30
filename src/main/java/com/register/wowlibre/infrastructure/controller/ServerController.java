package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.util.*;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/server")
public class ServerController {
    private final ServerPort serverPort;
    private final RandomString randomString;

    public ServerController(ServerPort serverPort, @Qualifier("random-string") RandomString randomString) {
        this.serverPort = serverPort;
        this.randomString = randomString;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid ServerDto serverDto) {

        final String apiKey = randomString.nextString();
        final String apiSecret = randomString.nextString();

        serverPort.create(ServerMapper.create(serverDto, apiKey, apiSecret, "", false), transactionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }
}
