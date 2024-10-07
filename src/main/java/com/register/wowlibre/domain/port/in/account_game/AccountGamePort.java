package com.register.wowlibre.domain.port.in.account_game;

import com.register.wowlibre.domain.dto.*;

public interface AccountGamePort {

    void create(Long userId, String serverName, String expansion, String username, String password,
                String transactionId);

    AccountsDto accounts(Long userId, int page, int size, String transactionId);

    VerifierAccountDto verify(Long userId, Long accountId, Long serverId, String transactionId);

    AccountDetailDto account(Long userId, Long accountId, Long serverId, String transactionId);

    MailsDto mails(Long userId, Long accountId, Long characterId, Long serverId, String transactionId);

    CharacterSocialDto friends(Long userId, Long accountId, Long characterId, Long serverId, String transactionId);

    void deleteFriend(Long userId, Long accountId, Long characterId, Long friendId, Long serverId,
                      String transactionId);

    void changePassword(Long userId, Long accountId, Long serverId, String password, String newPassword,
                        String transactionId);
}
