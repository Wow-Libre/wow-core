package com.register.wowlibre.infrastructure.repositories.user;

import com.register.wowlibre.domain.port.out.user.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaUserAdapter implements ObtainUserPort, SaveUserPort {
    private final UserRepository userRepository;

    public JpaUserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findByEmailAndStatusIsTrue(String email) {
        return userRepository.findByEmailAndStatusIsTrue(email);
    }

    @Override
    public Optional<UserEntity> findByCellPhoneAndStatusIsTrue(String cellPhone, String transactionId) {
        return userRepository.findByCellPhoneAndStatusIsTrue(cellPhone);
    }

    @Override
    public Optional<UserEntity> findByUserIdAndStatusIsTrue(Long userId, String transactionId) {
        return userRepository.findById(userId);
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable, String transactionId) {
        return userRepository.findAll(pageable);
    }

    @Override
    public long count(String transactionId) {
        return userRepository.count();
    }

    @Override
    public Page<UserEntity> findWebUsersPaginated(String emailFilter, Pageable pageable, String transactionId) {
        if (emailFilter != null && !emailFilter.isBlank()) {
            return userRepository.findByEmailContainingIgnoreCase(emailFilter.trim(), pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Override
    public long countWebUsers(String emailFilter, String transactionId) {
        if (emailFilter != null && !emailFilter.isBlank()) {
            return userRepository.countByEmailContainingIgnoreCase(emailFilter.trim());
        }
        return userRepository.count();
    }

    @Override
    public UserEntity save(UserEntity user, String transactionId) {
        return userRepository.save(user);
    }
}
