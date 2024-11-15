package com.register.wowlibre.domain.enums;

import com.register.wowlibre.domain.model.*;

import java.util.*;

public class CommandsCore {

    public static String sendItems(String playerName, String subject, String body, List<ItemQuantityModel> items) {
        StringBuilder commandBuilder = new StringBuilder(".send items ");
        commandBuilder.append(playerName).append(" \"").append(subject).append("\" \"").append(body).append("\" ");

        for (ItemQuantityModel item : items) {
            commandBuilder.append(item.id).append(":").append(item.quantity).append(" ");
        }

        return commandBuilder.toString().trim();
    }

    public static String sendMoney(String playerName, String subject, String body, String money) {
        return String.format(".send money %s \"%s\" \"%s\" %s", playerName, subject, body, money);
    }
}
