package ru.devsett.game.bunker;

import lombok.Getter;

public enum  Name {
    BUNKER(":shield: Бункер"),
    AREA("Площадь"),
    DESCRIPTION(":pencil2: Описание"),
    ROOMS(":sleeping_accommodation: Комнаты"),
    BAGGAGE_BUNKER(":package: Вещи"),
    LIVES(":microbe: В убежище живут"),
    DISASTER(":t_rex: Катаклизм"),
    JOB(":construction_worker: Профессия"),
    MALE_ANG_AGE(":bust_in_silhouette: Персонаж"),
    HUMAN_TRAIT(":face_with_monocle: Черта характера"),
    HEALTH(":heart: Здоровье"),
    HOBBY(":diving_mask: Хобби"),
    PHOBIA(":spider_web: Фобия"),
    BAGGAGE(":baggage_claim: Багаж"),
    ADDITIONAL_INFORMATION(":rainbow: Доп. информация"),
    FIRST_CART(":teddy_bear: Карта #1"),
    SECOND_CART(":teddy_bear: Карта #2"),
    END_DOT(": "),
    N("\n"),
    N_T("\n\t");
    @Getter
    private String description;

    private Name(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return description;
    }
}
