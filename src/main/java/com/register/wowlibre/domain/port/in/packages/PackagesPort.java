package com.register.wowlibre.domain.port.in.packages;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface PackagesPort {
    void save(PackagesEntity packagesEntity, String transactionId);

    List<ItemQuantityModel> findByProductId(ProductEntity product, String transactionId);
}
