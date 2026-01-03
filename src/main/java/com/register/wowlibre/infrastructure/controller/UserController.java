package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.user.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.security.*;
import com.register.wowlibre.domain.shared.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/account")
@Tag(name = "User Management", description = "APIs for user account management and authentication")
public class UserController {
    private final UserPort userPort;

    public UserController(UserPort userPort) {
        this.userPort = userPort;
    }

    @Operation(
            summary = "Create new user account",
            description = "Creates a new user account and returns JWT authentication tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User account created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict - User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(path = "/create")
    public ResponseEntity<GenericResponse<JwtDto>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid CreateUserDto createUser,
            HttpServletRequest request) {

        final JwtDto jwtDto = userPort.create(createUser, request.getRemoteAddr(), transactionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(jwtDto, transactionId).created().build());
    }

    @GetMapping(path = "/search")
    @Operation(
            summary = "Check user existence",
            description = "Checks if a user exists by email or cell phone.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existence status returned"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<UserExistenceDto>> search(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = "email", required = false) final String email,
            @RequestParam(name = "cell_phone", required = false) final String cellPhone) {

        if (email == null && cellPhone == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponseBuilder<UserExistenceDto>(transactionId).build());
        }

        boolean exists;

        if (email != null) {
            exists = Optional.ofNullable(userPort.findByEmail(email, transactionId)).isPresent();
        } else {
            exists = Optional.ofNullable(userPort.findByPhone(cellPhone, transactionId)).isPresent();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<UserExistenceDto>(transactionId)
                        .ok(new UserExistenceDto(exists)).build());
    }

    @GetMapping
    @Operation(
            summary = "Get user details",
            description = "Returns the user profile for the given user id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details returned"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<UserDetailDto>> detail(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId) {

        UserDetailDto userResponse = userPort.findByUserId(userId, transactionId)
                .map(model -> UserDetailDto.builder()
                        .id(model.getId())
                        .country(model.getCountry())
                        .dateOfBirth(model.getDateOfBirth())
                        .firstName(model.getFirstName())
                        .lastName(model.getLastName())
                        .cellPhone(model.getCellPhone())
                        .email(model.getEmail())
                        .rolName(model.getRolId().getName())
                        .status(model.getStatus())
                        .verified(model.getVerified())
                        .build())
                .orElse(null);

        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponseBuilder<UserDetailDto>(transactionId).build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<UserDetailDto>(transactionId).ok(userResponse)
                        .build());
    }

    @PutMapping("/email/confirmation")
    @Operation(
            summary = "Confirm email code",
            description = "Validates the email confirmation code for the account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid code or parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> validateEmailCodeForAccount(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam final String code) {

        userPort.validateEmailCodeForAccount(userId, code, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @GetMapping("/password-recovery/request")
    @Operation(
            summary = "Request password recovery code",
            description = "Sends a password recovery code to the provided email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recovery code sent"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> requestPasswordRecoveryCode(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam final String email) {

        userPort.generateRecoveryCode(email, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PutMapping("/password-recovery/confirm")
    @Operation(
            summary = "Confirm recovery code and reset password",
            description = "Validates the recovery code and resets the user's password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid code or email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> validateRecoveryCodeAndResetPassword(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestParam final String email,
            @RequestParam final String code) {

        userPort.resetPasswordWithRecoveryCode(email, code, locale, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PutMapping("/validated-mail/send")
    @Operation(
            summary = "Resend validation email",
            description = "Sends the email validation message to the provided email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation email sent"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> sendValidationEmail(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_EMAIL) final String email) {

        userPort.sendMailValidation(email, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PutMapping(path = "/user-password/change")
    @Operation(
            summary = "Change user password",
            description = "Changes the user's password using the current and new password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> newPassword(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody @Valid ChangePasswordUserDto changePassword) {

        userPort.changePassword(userId, changePassword.getPassword(),
                changePassword.getNewPassword(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
