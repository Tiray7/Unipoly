package ch.zhaw.it.pm3.unipoly;

import java.util.ArrayList;

public class Field {

    public Integer getLabel;
    private Config.FieldLabel label;
    private ArrayList<Player> players;

    public Field(Config.FieldLabel label) {
        this.label = label;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Config.FieldLabel getLabel() {
        return label;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
