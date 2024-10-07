package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.characters.*;
import com.register.wowlibre.domain.shared.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.register.wowlibre.domain.constant.Constants.*;
@RestController
@RequestMapping("/api/characters")
public class CharactersController {
    private final CharactersPort charactersPort;

    public CharactersController(CharactersPort charactersPort) {
        this.charactersPort = charactersPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<CharactersDto>> characters(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = PARAM_ACCOUNT_ID) final Long accountId,
            @RequestParam(name = PARAM_SERVER_ID) final Long serverId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId) {

        CharactersDto characters = charactersPort.characters(userId, accountId, serverId, transactionId);

        if (characters != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<CharactersDto>
                            (transactionId).ok(characters).build());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
