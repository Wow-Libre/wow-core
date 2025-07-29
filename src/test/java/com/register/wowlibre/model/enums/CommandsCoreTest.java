package com.register.wowlibre.model.enums;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CommandsCoreTest {
    @Test
    void testSendItemsWithMultipleItems() {
        List<ItemQuantityModel> items = Arrays.asList(
                new ItemQuantityModel("1001", 2),
                new ItemQuantityModel("2002", 5)
        );

        String command = CommandsCore.sendItems("PlayerOne", "Subject", "Body", items);
        assertEquals(".send items PlayerOne \"Subject\" \"Body\" 1001:2 2002:5", command);
    }

    @Test
    void testSendItemsWithSingleItem() {
        List<ItemQuantityModel> items = Collections.singletonList(new ItemQuantityModel("9999", 1));
        String command = CommandsCore.sendItems("TestPlayer", "Item Subject", "Some body", items);
        assertEquals(".send items TestPlayer \"Item Subject\" \"Some body\" 9999:1", command);
    }

    @Test
    void testSendItemsWithEmptyItemList() {
        List<ItemQuantityModel> items = Collections.emptyList();
        String command = CommandsCore.sendItems("EmptyPlayer", "No Items", "Empty body", items);
        assertEquals(".send items EmptyPlayer \"No Items\" \"Empty body\"", command);
    }

    @Test
    void testSendMoney() {
        String command = CommandsCore.sendMoney("MoneyPlayer", "Gold Subject", "Some gold body", "10000");
        assertEquals(".send money MoneyPlayer \"Gold Subject\" \"Some gold body\" 10000", command);
    }
}
