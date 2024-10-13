package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.guild.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/api/guilds")
public class GuildController {
    private final GuildPort guildPort;

    public GuildController(GuildPort guildPort) {
        this.guildPort = guildPort;
    }


    @GetMapping
    public ResponseEntity<GenericResponse<GuildsDto>> guilds(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam final Integer size,
            @RequestParam final Integer page,
            @RequestParam final String search,
            @RequestParam(required = false) final String server,
            @RequestParam(required = false) final String expansion) {

        GuildsDto guilds = guildPort.findAll(size, page, search, server, expansion, transactionId);

        if (guilds == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<GuildsDto>(transactionId).ok(guilds).build());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<GenericResponse<GuildDto>> guild(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable(name = "id") final Long guildId,
            @RequestParam(name = "server_id") final Long serverId) {

        GuildDto guild = guildPort.detail(serverId, guildId, transactionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<GuildDto>(transactionId).ok(guild).build());
    }


    @PostMapping(path = "/attach")
    public ResponseEntity<GenericResponse<Void>> attach(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody @Valid GuildAttachDto request) {

        guildPort.attach(request.getServerId(), userId, request.getAccountId(), request.getCharacterId(),
                request.getGuildId(), transactionId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
