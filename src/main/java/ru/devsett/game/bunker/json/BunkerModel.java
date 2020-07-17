package ru.devsett.game.bunker.json;

import lombok.Data;

@Data
public class BunkerModel {
    private String[] disasters;
    private String months;
    private String[] areas;
    private String[] eats;
    private String[] descriptions;
    private String[] lives;
    private String[] rooms;
    private String[] additionalItems;
 }
