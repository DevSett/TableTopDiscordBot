package ru.devsett.bot.util;

public enum  Role {
    WATCHER("Зритель"),
    PLAYER("Играет"),
    SUPPORT("Поддержка"),
    MASTER("Ведущий"),
    MAFCLUB("Мафклуб"),
    MODERATOR("Модератор"),
    EXPERT("Опытный игрок"),
    DEVELOPER("Разработчик"),
    ROLE_1("MAF I"),
    ROLE_2("MAF II"),
    ROLE_3("MAF III"),
    ROLE_4("MAF IV"),
    ROLE_5("MAF V"),
    BAN("Бан"),
    BANKIR("💰Банкир");

    private String name;

    private Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

