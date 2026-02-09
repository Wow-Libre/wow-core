package com.register.wowlibre.infrastructure.repositories.user;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>, CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndStatusIsTrue(final String email);

    Optional<UserEntity> findByCellPhoneAndStatusIsTrue(final String cellPhone);

    Page<UserEntity> findAll(Pageable pageable);

    Page<UserEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    long countByEmailContainingIgnoreCase(String email);

}
