package com.register.wowlibre.domain.port.out.user;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface ObtainUserPort {
    Optional<UserEntity> findByEmailAndStatusIsTrue(String email);

    Optional<UserEntity> findByCellPhoneAndStatusIsTrue(String cellPhone, String transactionId);

    Optional<UserEntity> findByUserIdAndStatusIsTrue(Long userId, String transactionId);

    Page<UserEntity> findAll(Pageable pageable, String transactionId);

    long count(String transactionId);

    Page<UserEntity> findWebUsersPaginated(String emailFilter, Pageable pageable, String transactionId);

    long countWebUsers(String emailFilter, String transactionId);

}
