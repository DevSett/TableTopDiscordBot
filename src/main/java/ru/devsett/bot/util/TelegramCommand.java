package ru.devsett.bot.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TelegramCommand {
    MUTE_ALL("muteall"),
    UNMUTE_ALL("unmuteall")
    ;

    @Getter
    private String msg;

    private TelegramCommand(String s) {
       this.msg = s;
    }

    public static List<String> allValues() {
       return Arrays.stream(TelegramCommand.values()).map(val -> val.msg).collect(Collectors.toList());
    }
}
