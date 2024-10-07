package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/account/game")
public class AccountGameController {
    private final AccountGamePort accountGamePort;

    public AccountGameController(AccountGamePort accountGamePort) {
        this.accountGamePort = accountGamePort;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody @Valid CreateAccountGameDto account) {

        accountGamePort.create(userId, account.getServerName(), account.getExpansion(), account.getUsername(),
                account.getPassword(), transactionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }

    @GetMapping(path = "/available")
    public ResponseEntity<GenericResponse<AccountsDto>> accounts(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam final int page, @RequestParam final int size) {

        AccountsDto accounts = accountGamePort.accounts(userId, page, size, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(accounts, transactionId).created().build());
    }


    @GetMapping(path = "/{account_id}/{server_id}")
    public ResponseEntity<GenericResponse<AccountDetailDto>> account(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @PathVariable final Long account_id,
            @PathVariable final Long server_id) {

        final AccountDetailDto account = accountGamePort.account(userId, account_id, server_id, transactionId);

        if (account != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<AccountDetailDto>(transactionId).ok(account).build());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/mails")
    public ResponseEntity<GenericResponse<MailsDto>> mails(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam(name = "account_id") final Long accountId,
            @RequestParam(name = "server_id") final Long serverId,
            @RequestParam(name = "character_id") final Long characterId) {

        final MailsDto mails = accountGamePort.mails(userId, accountId, characterId, serverId,
                transactionId);

        if (mails != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<MailsDto>(transactionId).ok(mails).build());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/social/friends")
    public ResponseEntity<GenericResponse<CharacterSocialDto>> friends(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam(name = "account_id") final Long accountId,
            @RequestParam(name = "server_id") final Long serverId,
            @RequestParam(name = "character_id") final Long characterId) {

        final CharacterSocialDto characterSocialDto = accountGamePort.friends(userId, accountId, characterId, serverId,
                transactionId);

        if (characterSocialDto != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<CharacterSocialDto>(transactionId).ok(characterSocialDto).build());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
