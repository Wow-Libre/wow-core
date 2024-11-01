package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/server")
public class ServerController {
    private final ServerPort serverPort;

    public ServerController(ServerPort serverPort) {
        this.serverPort = serverPort;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid ServerCreateDto serverCreateDto) {

        serverPort.create(serverCreateDto, transactionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }

    @GetMapping("/key")
    public ResponseEntity<GenericResponse<ServerModel>> apiKey(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = "api_key") String apiKey) {

        final ServerModel server = serverPort.findByApiKeyAndStatusIsTrue(apiKey, transactionId);

        if (server == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new GenericResponseBuilder<ServerModel>(transactionId).notContent().build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(server, transactionId).ok().build());
    }


    @GetMapping
    public ResponseEntity<GenericResponse<List<ServersDto>>> servers(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

        final List<ServersDto> serverList = serverPort.findByStatusIsTrue(transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(serverList, transactionId).ok().build());
    }



}
