package com.register.wowlibre.domain.mapper;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.*;

public class UserMapper {


    public static UserEntity toEntity(UserModel user) {
        if (user == null) {
            return null;
        }

        UserEntity serverEntity = new UserEntity();
        serverEntity.setId(user.id);
        serverEntity.setPassword(user.password);
        serverEntity.setStatus(user.status);
        serverEntity.setVerified(user.verified);
        serverEntity.setLanguage(user.language);
        serverEntity.setRolId(RolMapper.toEntity(user.rolModel));
        serverEntity.setCountry(user.country);
        serverEntity.setLastName(user.lastName);
        serverEntity.setAvatarUrl(user.avatar);
        return serverEntity;
    }


}
