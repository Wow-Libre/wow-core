package com.register.wowlibre.domain.model;


import lombok.Builder;

import java.time.LocalDate;

@Builder
public class UserModel {
    public Long id;
    public String country;
    public LocalDate dateOfBirth;
    public String firstName;
    public String lastName;
    public String cellPhone;
    public String email;
    public String password;
    public boolean status;
    public boolean verified;
    public String language;
    public RolModel rolModel;
}
