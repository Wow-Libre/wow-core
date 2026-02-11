package com.register.wowlibre.application.services.web_users;

import com.register.wowlibre.domain.dto.user.WebUserAccountGameDto;
import com.register.wowlibre.domain.dto.user.WebUserRowDto;
import com.register.wowlibre.domain.dto.user.WebUsersPageDto;
import com.register.wowlibre.domain.port.in.web_users.WebUsersDashboardPort;
import com.register.wowlibre.domain.port.out.account_game.ObtainAccountGamePort;
import com.register.wowlibre.domain.port.out.user.ObtainUserPort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.UserEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebUsersDashboardService implements WebUsersDashboardPort {

    private final ObtainUserPort obtainUserPort;
    private final ObtainAccountGamePort obtainAccountGamePort;

    public WebUsersDashboardService(ObtainUserPort obtainUserPort,
                                    ObtainAccountGamePort obtainAccountGamePort) {
        this.obtainUserPort = obtainUserPort;
        this.obtainAccountGamePort = obtainAccountGamePort;
    }

    @Override
    public WebUsersPageDto getWebUsersPage(String emailFilter, int page, int size, String transactionId) {
        var pageable = PageRequest.of(page, size);
        var userPage = obtainUserPort.findWebUsersPaginated(emailFilter, pageable, transactionId);

        List<WebUserRowDto> content = userPage.getContent().stream()
                .map(user -> mapToWebUserRow(user, transactionId))
                .collect(Collectors.toList());

        return WebUsersPageDto.builder()
                .content(content)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .size(userPage.getSize())
                .number(userPage.getNumber())
                .build();
    }

    private WebUserRowDto mapToWebUserRow(UserEntity user, String transactionId) {
        List<AccountGameEntity> accountGames = obtainAccountGamePort.findAllByUserId(user.getId(), transactionId);
        List<WebUserAccountGameDto> accounts = accountGames.stream()
                .map(this::mapToAccountGameDto)
                .collect(Collectors.toList());

        String rolName = user.getRolId() != null ? user.getRolId().getName() : null;

        return WebUserRowDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .language(user.getLanguage())
                .status(user.getStatus())
                .verified(user.getVerified())
                .rolName(rolName)
                .accountCount(accounts.size())
                .accounts(accounts)
                .build();
    }

    private WebUserAccountGameDto mapToAccountGameDto(AccountGameEntity ag) {
        String realmName = ag.getRealmId() != null ? ag.getRealmId().getName() : null;
        Long realmId = ag.getRealmId() != null ? ag.getRealmId().getId() : null;
        return WebUserAccountGameDto.builder()
                .id(ag.getId())
                .username(ag.getUsername())
                .gameEmail(ag.getGameEmail())
                .accountId(ag.getAccountId())
                .status(ag.isStatus())
                .realmId(realmId)
                .realmName(realmName)
                .build();
    }
}
