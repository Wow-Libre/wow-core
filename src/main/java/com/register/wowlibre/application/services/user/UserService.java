package com.register.wowlibre.application.services.user;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.dto.user.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.google.*;
import com.register.wowlibre.domain.port.in.jwt.*;
import com.register.wowlibre.domain.port.in.mail.*;
import com.register.wowlibre.domain.port.in.rol.*;
import com.register.wowlibre.domain.port.in.security_validation.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.user.*;
import com.register.wowlibre.domain.security.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.config.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class UserService implements UserPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String PICTURE_DEFAULT_PROFILE_WEB = "https://static.wixstatic.com/media/" +
            "5dd8a0_bc62a8e593dd44cfb14a5c308ebfacf9~mv2.png";

    /**
     * USERS PORT
     **/
    private final ObtainUserPort obtainUserPort;
    private final SaveUserPort saveUserPort;
    /**
     * EXTERNAL
     **/
    private final PasswordEncoder passwordEncoder;
    private final I18nService i18nService;
    private final RandomString randomString;
    /**
     * Rol Port
     **/
    private final RolPort rolPort;
    /**
     * MAILS - JWT SECURITY
     **/
    private final JwtPort jwtPort;
    private final SecurityValidationPort securityValidationPort;
    /**
     * CLIENTS
     **/
    private final GooglePort googlePort;
    private final MailPort mailPort;

    /**
     * Configurations
     **/
    private final Configurations configurations;

    public UserService(ObtainUserPort obtainUserPort, SaveUserPort saveUserPort, PasswordEncoder passwordEncoder,
                       JwtPort jwtPort, RolPort rolPort, MailPort mailPort,
                       SecurityValidationPort securityValidationPort,
                       I18nService i18nService, @Qualifier("reset-password-string") RandomString randomString,
                       GooglePort googlePort, Configurations configurations) {
        this.obtainUserPort = obtainUserPort;
        this.saveUserPort = saveUserPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
        this.rolPort = rolPort;
        this.mailPort = mailPort;
        this.securityValidationPort = securityValidationPort;
        this.i18nService = i18nService;
        this.randomString = randomString;
        this.googlePort = googlePort;
        this.configurations = configurations;
    }

    private static UserEntity create(CreateUserDto createUserDto, String password, RolEntity rolModel) {
        UserEntity userRegister = new UserEntity();
        userRegister.setAvatarUrl(PICTURE_DEFAULT_PROFILE_WEB);
        userRegister.setEmail(createUserDto.getEmail());
        userRegister.setCountry(createUserDto.getCountry());
        userRegister.setFirstName(createUserDto.getFirstName());
        userRegister.setCellPhone(createUserDto.getCellPhone());
        userRegister.setLanguage(createUserDto.getLanguage());
        userRegister.setPassword(password);
        userRegister.setLastName(createUserDto.getLastName());
        userRegister.setDateOfBirth(createUserDto.getDateOfBirth());
        userRegister.setStatus(true);
        userRegister.setVerified(false);
        userRegister.setRolId(rolModel);
        return userRegister;
    }

    @Override
    public JwtDto create(CreateUserDto createUserDto, String ip, Locale locale, String transactionId) {

        if (!googlePort.verifyCaptcha(configurations.getGoogleSecret(), createUserDto.getToken(), ip,
                transactionId)) {
            LOGGER.error("[UserService] [create] An error occurred while verifying the captcha. -  " +
                    "[TransactionId: {}]", transactionId);
            throw new InternalException("The captcha is invalid", transactionId);
        }

        final String email = createUserDto.getEmail();

        if (findByEmail(email, transactionId) != null) {
            LOGGER.error("[UserService] [create]  There is already a registered customer with this information. " +
                    "Id: {}]", transactionId);
            throw new FoundException("There is already a registered client with this data", transactionId);
        }

        final RolEntity rolModel = rolPort.findByName(Rol.CLIENT.name(), transactionId);

        if (rolModel == null) {
            LOGGER.error("[UserService] [create] An error occurred while assigning a role. - [transactionId: {}]",
                    transactionId);
            throw new InternalException("An unexpected error has occurred", transactionId);
        }

        final String passwordEncode = passwordEncoder.encode(createUserDto.getPassword());

        UserEntity userRegister = create(createUserDto, passwordEncode, rolModel);

        final UserEntity user = saveUserPort.save(userRegister, transactionId);
        final boolean isAdmin = false;
        final boolean requiredValidationMail = true;

        CustomUserDetails customUserDetails = new CustomUserDetails(
                List.of(rolModel),
                passwordEncode,
                email,
                true,
                true,
                true,
                user.getStatus(),
                user.getId(),
                user.getAvatarUrl(),
                createUserDto.getLanguage(),
                isAdmin
        );

        final String token = jwtPort.generateToken(customUserDetails);
        final Date expiration = jwtPort.extractExpiration(token);
        final String refreshToken = jwtPort.generateRefreshToken(customUserDetails);

        return new JwtDto(user.getId(), token, refreshToken, expiration, user.getAvatarUrl(),
                customUserDetails.getLanguage(), requiredValidationMail, isAdmin);
    }

    @Override
    public UserModel findByEmail(String email, String transactionId) {
        return findByEmailEntity(email, transactionId).map(UserEntity::mapToModelEntity).orElse(null);
    }

    @Override
    public UserModel findByPhone(String cellPhone, String transactionId) {
        return obtainUserPort.findByCellPhoneAndStatusIsTrue(cellPhone, transactionId).map(UserEntity::mapToModelEntity)
                .orElse(null);
    }

    @Override
    public Optional<UserEntity> findByUserId(Long userId, String transactionId) {
        return obtainUserPort.findByUserIdAndStatusIsTrue(userId, transactionId);
    }

    @Override
    public Optional<UserEntity> findByEmailEntity(String email, String transactionId) {
        return obtainUserPort.findByEmailAndStatusIsTrue(email);
    }

    @Override
    public void validateEmailCodeForAccount(Long userId, String code, String transactionId) {

        final Optional<UserEntity> userFound = findByUserId(userId, transactionId);

        if (userFound.isEmpty() || !userFound.get().getStatus()) {
            LOGGER.error("[UserService] [create] The email cannot be validated because the user is in an invalid " +
                    "state. [Id: {}]", transactionId);
            throw new InternalException("It was not possible to validate your code, the " +
                    "account is disabled or does not exist.", transactionId);
        }

        if (userFound.get().getVerified()) {
            LOGGER.info("User email already verified. [Id: {}]", transactionId);
            return;
        }

        UserEntity userModel = userFound.get();

        final String obtainedCode = securityValidationPort.findByCodeEmailValidation(userModel.getEmail(),
                transactionId);

        if (obtainedCode != null && obtainedCode.equals(code)) {
            userModel.setVerified(true);
            saveUserPort.save(userModel, transactionId);
        } else {
            throw new InternalException("The codes are invalid", transactionId);
        }
    }

    @Override
    public void generateRecoveryCode(String email, String transactionId) {

        Optional<UserEntity> account = findByEmailEntity(email, transactionId);

        if (account.isEmpty() || !account.get().getStatus()) {
            LOGGER.error("[UserService] [generateRecoveryCode] The account does not exist or is disabled from " +
                    "generating a recovery code. " +
                    "[ID]: {}", transactionId);
            throw new InternalException("The email entered does not exist", transactionId);
        }

        Locale locale = Locale.forLanguageTag(account.get().getLanguage());

        final String codeOtp = securityValidationPort.generateOtpRecoverAccount(account.get().getEmail(),
                transactionId);

        final String body = i18nService.tr("recovery-password-body", new Object[]{codeOtp.toUpperCase()}, locale);
        final String subject = i18nService.tr("recovery-password-subject", locale);

        mailPort.sendMail(account.get().getEmail(), subject, body, transactionId);
    }

    @Override
    public void resetPasswordWithRecoveryCode(String email, String code, Locale locale, String transactionId) {

        final String codeObtain = securityValidationPort.findByOtpRecoverPassword(email, transactionId);

        if (codeObtain == null) {
            LOGGER.error("[UserService] [resetPasswordWithRecoveryCode] The code has expired  [ID] {}", transactionId);
            throw new InternalException("The code has expired", transactionId);
        }

        if (!codeObtain.toUpperCase().equals(code)) {
            LOGGER.error("[UserService] [resetPasswordWithRecoveryCode] The security code is invalid  [ID] {}",
                    transactionId);
            throw new InternalException("The security code is invalid", transactionId);
        }

        final Optional<UserEntity> account = findByEmailEntity(email, transactionId);

        if (account.isEmpty()) {
            LOGGER.error("[UserService] [resetPasswordWithRecoveryCode] The account could not be retrieved when " +
                    "searching for the customer's email or it is in an inactive status. [ID] {}", transactionId);
            throw new InternalException("It was not possible to assign a new password, please contact support",
                    transactionId);
        }

        UserEntity user = account.get();
        final String password = randomString.nextString();
        final String body = i18nService.tr("message-new-password-body", new Object[]{password}, locale);
        final String subject = i18nService.tr("message-new-password-subject", locale);

        mailPort.sendMail(account.get().getEmail(), subject, body, transactionId);

        user.setPassword(passwordEncoder.encode(password));

        saveUserPort.save(user, transactionId);
        securityValidationPort.resetOtpValidation(email, transactionId);
    }

    @Override
    public void sendMailValidation(String mail, String transactionId) {
        final Optional<UserEntity> account = findByEmailEntity(mail, transactionId);

        if (account.isEmpty()) {
            LOGGER.error("[UserService] [sendMailValidation] The account could not be retrieved when " +
                    "searching for the customer's email or it is in an inactive status. [Id] {}", transactionId);
            throw new InternalException("Please contact support", transactionId);
        }

        UserEntity user = account.get();

        if (user.getVerified()) {
            return;
        }

        Locale locale = Locale.forLanguageTag(account.get().getLanguage());
        final String code = securityValidationPort.generateCodeValidationMail(user.getEmail(), transactionId);
        final String body = i18nService.tr("send-code-mail-validation-mail", new Object[]{code.toUpperCase()}, locale);

        mailPort.sendCodeMail(user.getEmail(), body, code, locale, transactionId);
    }

    @Override
    public void changePassword(Long userId, String password, String newPassword, String transactionId) {

        final Optional<UserEntity> userFound = findByUserId(userId, transactionId);

        if (userFound.isEmpty() || !userFound.get().getStatus()) {
            LOGGER.error("[UserService] [changePassword] The email cannot be validated because the user is in an " +
                    "invalid state. [Id: {}]", transactionId);
            throw new InternalException("It was not possible to change the password because the account cannot be " +
                    "found", transactionId);
        }

        UserEntity user = userFound.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InternalException("The current password entered is invalid", transactionId);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        saveUserPort.save(user, transactionId);
    }

}
