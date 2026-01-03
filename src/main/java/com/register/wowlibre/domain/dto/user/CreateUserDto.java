package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.*;
import org.springframework.format.annotation.*;

import java.time.*;

@Data
@Schema(description = "User account creation data")
public class UserDto {
    @NotNull
    @Length(min = 2, max = 30)
    @Schema(description = "User's country code", example = "US", minLength = 2, maxLength = 30)
    private String country;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    @Schema(description = "User's date of birth", example = "1990-01-15", pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull
    @Length(min = 3, max = 30)
    @JsonProperty("first_name")
    @Schema(description = "User's first name", example = "John", minLength = 3, maxLength = 30)
    private String firstName;

    @NotNull
    @Length(min = 3, max = 30)
    @JsonProperty("last_name")
    @Schema(description = "User's last name", example = "Doe", minLength = 3, maxLength = 30)
    private String lastName;

    @JsonProperty("cell_phone")
    private String cellPhone;

    @NotNull
    @Length(min = 5, max = 50)
    @Email
    @Schema(description = "User's email address", example = "john.doe@example.com", minLength = 5, maxLength = 50)
    private String email;

    @Length(max = 30)
    @NotNull
    @Schema(description = "User's password", example = "SecurePass123!", maxLength = 30)
    private String password;

    @NotNull
    @Length(min = 1, max = 4)
    @Schema(description = "User's preferred language", example = "en", allowableValues = {"en", "es",
            "pt"}, minLength = 1, maxLength = 4)
    private String language;

    @NotNull
    @Schema(description = "CAPTCHA verification token", example = "03AGdBq25...")
    private String token;
}
