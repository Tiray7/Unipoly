package com.example.Softwareproject3;

import java.util.ArrayList;

public class Field {

    private String name;
    private Config.FieldLabel label;
    private ArrayList<Player> players;

    public Field(String name, Config.FieldLabel label){
        this.name=name;
        this.label =label;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public Config.FieldLabel getLabel() {
        return label;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }


}
