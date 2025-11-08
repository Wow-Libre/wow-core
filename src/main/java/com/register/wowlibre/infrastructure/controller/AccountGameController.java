package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.account_game.AccountGameDetailDto;
import com.register.wowlibre.domain.dto.account_game.AccountGameStatsDto;
import com.register.wowlibre.domain.dto.account_game.AccountsGameDto;
import com.register.wowlibre.domain.dto.account_game.CreateAccountGameDto;
import com.register.wowlibre.domain.port.in.account_game.AccountGamePort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/account/game")
@Tag(name = "Account Game", description = "APIs for managing game accounts")
public class AccountGameController {
    private final AccountGamePort accountGamePort;

    public AccountGameController(AccountGamePort accountGamePort) {
        this.accountGamePort = accountGamePort;
    }

    @Operation(summary = "Get user game accounts", description = "Retrieves paginated list of game accounts for the user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/available")
    public ResponseEntity<GenericResponse<AccountsGameDto>> accounts(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realm,
            @RequestParam final int page,
            @RequestParam final int size) {

        AccountsGameDto accounts = accountGamePort.accounts(userId, page, size, username, realm, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(accounts, transactionId).ok().build());
    }

    @Operation(summary = "Create game account", description = "Creates a new game account for the user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(path = "/create")
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody @Valid CreateAccountGameDto account) {

        accountGamePort.create(userId, account.getRealmName(), account.getExpansion(), account.getUsername(),
                account.getGameMail(), account.getPassword(), transactionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }


    @Operation(summary = "Get accounts by realm", description = "Retrieves all game accounts for a specific realm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<GenericResponse<AccountsGameDto>> accountsByRealm(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam(name = PARAM_SERVER_ID) Long serverId) {

        AccountsGameDto accounts = accountGamePort.accounts(userId, serverId, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(accounts, transactionId).ok().build());
    }


    @Operation(summary = "Get account details", description = "Retrieves detailed information for a specific game account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{account_id}/{realm_id}")
    public ResponseEntity<GenericResponse<AccountGameDetailDto>> detail(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @PathVariable final Long account_id,
            @PathVariable(name = "realm_id") final Long realmId) {

        final AccountGameDetailDto account = accountGamePort.account(userId, account_id, realmId, transactionId);

        if (account != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<AccountGameDetailDto>(transactionId).ok(account).build());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Deactivate accounts", description = "Deactivates one or more game accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid account IDs"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(path = "/inactive")
    public ResponseEntity<GenericResponse<Void>> inactive(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody List<Long> ids) {

        accountGamePort.desactive(ids, userId, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }

    @Operation(summary = "Get account statistics", description = "Retrieves statistics about user's game accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/stats")
    public ResponseEntity<GenericResponse<AccountGameStatsDto>> stats(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId) {

        AccountGameStatsDto stats = accountGamePort.stats(userId, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(stats, transactionId).ok().build());
    }
}
