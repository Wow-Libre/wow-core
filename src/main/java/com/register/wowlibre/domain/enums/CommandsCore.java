package com.register.wowlibre.domain.enums;

public class CommandsCore {


    public static String sendMoney(String playerName, String subject, String body, String money) {
        return String.format(".send money %s \"%s\" \"%s\" %s", playerName, subject, body, money);
    }
}
