package ru.devsett.game.bunker;

import lombok.Data;

@Data
public class Character {
    private String maleAngAge;
    private String humanTrait;
    private String job;
    private String health;
    private String hobby;
    private String phobia;
    private String baggage;
    private String additionalInformation;
    private String firstCart;
    private String secondCart;

    private String age;

    @Override
    public String toString() {
        return "" + Name.JOB + Name.END_DOT +  job
                + Name.N + Name.MALE_ANG_AGE + Name.END_DOT + maleAngAge
                + Name.N + Name.HUMAN_TRAIT + Name.END_DOT + humanTrait
                + Name.N + Name.HEALTH + Name.END_DOT + health
                + Name.N + Name.HOBBY + Name.END_DOT + hobby
                + Name.N + Name.PHOBIA + Name.END_DOT + phobia
                + Name.N + Name.BAGGAGE + Name.END_DOT + baggage
                + Name.N + Name.ADDITIONAL_INFORMATION + Name.END_DOT + additionalInformation
                + Name.N + Name.FIRST_CART + Name.END_DOT + firstCart
                + Name.N + Name.SECOND_CART + Name.END_DOT + secondCart;
    }
}
