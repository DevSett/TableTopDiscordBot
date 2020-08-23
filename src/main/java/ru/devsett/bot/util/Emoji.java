package ru.devsett.bot.util;

public enum Emoji {
    GAME("\uD83C\uDFAD"),
    CANCEL("❌"),
    ALERT("⚠️"),
    BLACK("blackcat1:739484327087177748"),
    RED("redcat2:739484326810353724"),
    ONE("1️⃣"),
    TWO("2️⃣"),
    THREE("3️⃣"),
    FOUR("4️⃣"),
    FIVE("5️⃣"),
    SIX("6️⃣"),
    SEVEN("7️⃣"),
    EIGHT("8️⃣"),
    NINE("9️⃣"),
    TEN("\uD83D\uDD1F"),
    SECOND_FIRST("⏩"),
    SECOND_TWO("⏭️"),
    WIN("\uD83C\uDFC6");

    private String name;

    private Emoji(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
