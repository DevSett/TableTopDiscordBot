package ru.devsett.bot.util;

import lombok.Getter;

public enum  TypeChannel {
    DEFAULT_CHANNEL(0),
    MASTER_CHANNEL(1),
    BAN_CHANNEL(2),
    ROLE_CHANNEL(3),
    NEWS_CHANNEL(4),
    NEWS_ADD_CHANNEL(5),
    JOIN_CHANNEL(6);

    @Getter
    private Integer subtype;
    TypeChannel(Integer number) {
        subtype = number;
    }
}
