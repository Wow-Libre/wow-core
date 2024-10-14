package com.register.wowlibre.domain.security;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.out.user.ObtainUserPort;
import com.register.wowlibre.domain.shared.CustomUserDetails;
import com.register.wowlibre.infrastructure.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.Errors.CONSTANT_GENERIC_ERROR_ACCOUNT_IS_NOT_AVAILABLE;

@Component
public class UserDetailsServiceCustom implements UserDetailsService {
    private final ObtainUserPort obtainUserPort;

    public UserDetailsServiceCustom(ObtainUserPort obtainUserPort) {
        this.obtainUserPort = obtainUserPort;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity account = obtainUserPort.findByEmailAndStatusIsTrue(username)
                .orElseThrow(() -> new UnauthorizedException(CONSTANT_GENERIC_ERROR_ACCOUNT_IS_NOT_AVAILABLE + username
                        , ""));

        return new CustomUserDetails(assignRol(account.getRolId().getName()), account.getPassword(),
                account.getEmail(),
                true,
                true,
                true,
                account.getStatus(),
                account.getId(),
                account.getAvatarUrl(),
                account.getLanguage()
        );
    }


    private List<GrantedAuthority> assignRol(String name) {
        return Collections.singletonList(new SimpleGrantedAuthority(name));
    }


}
