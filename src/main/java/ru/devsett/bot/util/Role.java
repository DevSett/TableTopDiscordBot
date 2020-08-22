package ru.devsett.bot.util;

public enum  Role {
    WATCHER("Зритель"),
    PLAYER("Играет"),
    SUPPORT("Поддержка"),
    MASTER("Ведущий"),
    MODERATOR("Модератор"),
    EXPERT("Опытный игрок"),
    BAN("Бан");

    private String name;

    private Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

