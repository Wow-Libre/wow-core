package com.register.wowlibre.domain.port.in.integrator;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.model.*;

public interface IntegratorPort {
    Long create(String username, String password, ServerModel server, UserModel userModel,
                String transactionId);

    CharactersDto characters(ServerModel server, Long accountId, Long userId, String transactionId);

    AccountDetailResponse account(String host, String jwt, Long accountId, String transactionId);

    MailsDto mails(String host, String jwt, Long characterId, String transactionId);

    void deleteFriend(String host, String jwt, Long characterId, Long friendId, Long accountId,
                      String transactionId);

    CharacterSocialDto friends(String host, String jwt, Long characterId, String transactionId);

    void changePassword(String host, String jwt, Long accountId, String transactionId);
}
