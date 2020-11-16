package ch.zhaw.it.pm3.unipoly;

import java.util.ArrayList;

public class Field {

    public Integer getLabel;
    private Config.FieldLabel label;
    private ArrayList<Player> players;
    private String explanation;

    public Field(Config.FieldLabel label, String explanation) {
        this.label = label;
        this.explanation = explanation;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Config.FieldLabel getLabel() {
        return label;
    }

    public String getExplanation() {
        return explanation;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setOwnerIndex(int index) {
    }
}
