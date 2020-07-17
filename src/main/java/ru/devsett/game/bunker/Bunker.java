package ru.devsett.game.bunker;

import lombok.Data;

import java.util.List;

@Data
public class Bunker {

    private String disaster;
    private String months;
    private String area;
    private String eat;
    private String description;
    private String live;
    private List<String> rooms;
    private List<String> additionalItems;
    private List<Character> characterList;

    public String toStringWithoutDisaster() {
        return  "" + Name.BUNKER + Name.END_DOT + Name.N_T + Name.AREA + Name.END_DOT + area
                + Name.N_T + "Время нахождения в убежище (" + eat + ")" + Name.END_DOT + months
                + Name.N_T + Name.DESCRIPTION + Name.END_DOT + description
                + Name.N_T + Name.ROOMS + Name.END_DOT + String.join(", ", rooms)
                + Name.N_T + Name.BAGGAGE_BUNKER + Name.END_DOT + String.join(", ", additionalItems)
                + Name.N_T + Name.LIVES + Name.END_DOT + live;
    }
    @Override
    public String toString() {
        return "" + Name.DISASTER + Name.END_DOT + disaster
                + Name.N + Name.BUNKER + Name.END_DOT + Name.N_T + Name.AREA + Name.END_DOT + area
                + Name.N_T + "Время нахождения в убежище (" + eat + ")" + Name.END_DOT + months
                + Name.N_T + Name.DESCRIPTION + Name.END_DOT + description
                + Name.N_T + Name.ROOMS + Name.END_DOT + String.join(", ", rooms)
                + Name.N_T + Name.BAGGAGE_BUNKER + Name.END_DOT + String.join(", ", additionalItems)
                + Name.N_T + Name.LIVES + Name.END_DOT + live;
    }
}
