package ru.devsett.bot.util;

public enum  Role {
    WATCHER("Зритель"),
    PLAYER("Мафия"),
    MASTER("Ведущий"),
    MODERATOR("Модератор"),
    EXPERT("Опытный"),
    BAN("Бан");

    private String name;

    private Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

