package com.register.wowlibre.infrastructure.repositories.user;

import com.register.wowlibre.infrastructure.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndStatusIsTrue(final String email);

    Optional<UserEntity> findByCellPhoneAndStatusIsTrue(final String cellPhone);

}
