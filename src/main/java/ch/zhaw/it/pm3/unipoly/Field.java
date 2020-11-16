package ch.zhaw.it.pm3.unipoly;

public class Field {

    public Integer getLabel;
    private Config.FieldLabel label;

    public Field(Config.FieldLabel label) {
        this.label = label;
    }

    public Config.FieldLabel getLabel() {
        return label;
    }

}
