package com.register.wowlibre.domain.port.in.integrator;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface IntegratorPort {
    Long create(String username, String password, ServerModel server, UserModel userModel,
                String transactionId);

    CharactersDto characters(String host, String jwt, Long accountId, Long userId, String transactionId);

    AccountDetailResponse account(String host, String jwt, Long accountId, String transactionId);

    MailsDto mails(String host, String jwt, Long characterId, String transactionId);

    void deleteFriend(String host, String jwt, Long characterId, Long friendId, Long accountId, Long userId,
                      String transactionId);

    CharacterSocialDto friends(String host, String jwt, Long characterId, String transactionId);

    void changePassword(String host, String apiSecret, String jwt, Long accountId, Long userId, String password,
                        String transactionId);

    List<CharacterProfessionsDto> professions(String host, String jwt, Long accountId, Long characterId,
                                              String transactionId);

    void sendMoney(String host, String jwt, Long accountId, Long userId, Long characterId, Long friendId, Long money,
                   Double cost, String transactionId);

    void sendLevel(String host, String jwt, Long accountId, Long userId, Long characterId, Long friendId,
                   Integer level, Double cost, String transactionId);
}
