package com.register.wowlibre.application.services.user;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.jwt.*;
import com.register.wowlibre.domain.port.in.rol.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.user.*;
import com.register.wowlibre.domain.security.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class UserService implements UserPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private static final String PICTURE_DEFAULT_PROFILE_WEB = "https://i.ibb.co/M8Kfq9X/icon-Default.png";
    private final ObtainUserPort obtainUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtPort jwtPort;
    private final RolPort rolPort;


    public UserService(ObtainUserPort obtainUserPort, SaveUserPort saveUserPort, PasswordEncoder passwordEncoder,
                       JwtPort jwtPort,
                       RolPort rolPort) {
        this.obtainUserPort = obtainUserPort;
        this.saveUserPort = saveUserPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
        this.rolPort = rolPort;
    }


    @Override
    public JwtDto create(UserDto userDto, String transactionId) {

        if (findByEmail(userDto.getEmail(), transactionId) != null) {
            throw new FoundException("There is already a registered client with this data", transactionId);
        }

        final String passwordEncode = passwordEncoder.encode(userDto.getPassword());

        final RolModel rolModel = rolPort.findByName(Roles.CLIENT.getRoleName(), transactionId);

        if (rolModel == null) {
            LOGGER.error("An error occurred while assigning a role.  TransactionId: [{}]",
                    transactionId);
            throw new NotFoundException("An error occurred while assigning a role.", transactionId);
        }

        userDto.setPassword(passwordEncode);
        final UserEntity user = saveUserPort.save(mapToModel(userDto, rolModel), transactionId);

        CustomUserDetails customUserDetails = new CustomUserDetails(
                List.of(rolModel),
                passwordEncode,
                userDto.getEmail(),
                true,
                true,
                true,
                user.getStatus(),
                user.getId(),
                user.getAvatarUrl(),
                userDto.getLanguage()
        );

        final String token = jwtPort.generateToken(customUserDetails);
        final Date expiration = jwtPort.extractExpiration(token);
        final String refreshToken = jwtPort.generateRefreshToken(customUserDetails);

        return new JwtDto(token, refreshToken, expiration, PICTURE_DEFAULT_PROFILE_WEB,
                customUserDetails.getLanguage());
    }

    @Override
    public UserModel findByEmail(String email, String transactionId) {
        return obtainUserPort.findByEmailAndStatusIsTrue(email).map(UserEntity::mapToModelEntity).orElse(null);
    }

    @Override
    public UserModel findByPhone(String cellPhone, String transactionId) {
        return obtainUserPort.findByCellPhoneAndStatusIsTrue(cellPhone, transactionId).map(UserEntity::mapToModelEntity)
                .orElse(null);
    }

    @Override
    public UserModel findByUserId(Long userId, String transactionId) {
        return obtainUserPort.findByUserIdAndStatusIsTrue(userId, transactionId).map(UserEntity::mapToModelEntity)
                .orElse(null);
    }

    private UserEntity mapToModel(UserDto userDto, RolModel rolModel) {
        UserEntity user = new UserEntity();
        user.setAvatarUrl(PICTURE_DEFAULT_PROFILE_WEB);
        user.setEmail(userDto.getEmail());
        user.setCountry(userDto.getCountry());
        user.setFirstName(userDto.getFirstName());
        user.setCellPhone(userDto.getCellPhone());
        user.setLanguage(userDto.getLanguage());
        user.setPassword(userDto.getPassword());
        user.setLastName(userDto.getLastName());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setStatus(true);
        user.setVerified(false);
        user.setRolId(RolMapper.toEntity(rolModel));
        return user;
    }
}
