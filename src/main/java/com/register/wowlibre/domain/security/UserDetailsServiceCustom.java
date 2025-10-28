package com.register.wowlibre.domain.security;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.out.user.ObtainUserPort;
import com.register.wowlibre.domain.shared.CustomUserDetails;
import com.register.wowlibre.infrastructure.entities.UserEntity;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class UserDetailsServiceCustom implements UserDetailsService {

    private final ObtainUserPort obtainUserPort;

    public UserDetailsServiceCustom(ObtainUserPort obtainUserPort) {
        this.obtainUserPort = obtainUserPort;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity account = obtainUserPort.findByEmailAndStatusIsTrue(username)
                .orElseThrow(() -> new UnauthorizedException("The data supplied is valid or not found" + username
                        , "[loadUserByUsername]"));

        final String rolName = account.getRolId().getName();
        boolean isAdmin = Rol.ADMIN.getName().equals(rolName);

        return new CustomUserDetails(assignRol(rolName), account.getPassword(),
                account.getEmail(),
                true,
                true,
                true,
                account.getStatus(),
                account.getId(),
                account.getAvatarUrl(),
                account.getLanguage(),
                !account.getVerified(),
                isAdmin
        );
    }

    private List<GrantedAuthority> assignRol(String name) {
        return Collections.singletonList(new SimpleGrantedAuthority(name));
    }

}
