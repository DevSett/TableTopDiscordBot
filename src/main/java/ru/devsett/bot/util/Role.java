package ru.devsett.bot.util;

public enum  Role {
    WATCHER("Зритель"),
    PLAYER("Мафия"),
    MASTER("Ведущий"),
    EXPERT("Опытный");

    private String name;

    private Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

