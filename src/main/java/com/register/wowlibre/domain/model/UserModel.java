package com.register.wowlibre.domain.model;


import com.register.wowlibre.infrastructure.entities.*;
import lombok.*;

import java.time.*;

@Builder
public class UserModel {
    public Long id;
    public String country;
    public LocalDate dateOfBirth;
    public String firstName;
    public String lastName;
    public String cellPhone;
    public String password;
    public String email;
    public boolean status;
    public boolean verified;
    public String avatar;
    public String language;
    public RolEntity rolModel;
}
