package com.register.wowlibre.domain.port.in.transaction;

import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface TransactionPort {
    void purchase(Long serverId, Long userId, Long accountId, String reference, List<ItemQuantityModel> items,
                  String transactionId);
}
