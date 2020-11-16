package ch.zhaw.it.pm3.unipoly;

public class Field {

    public Integer getLabel;
    private Config.FieldLabel label;
    private String explanation;

    public Field(Config.FieldLabel label, String explanation) {
        this.label = label;
        this.explanation = explanation;
    }

    public Config.FieldLabel getLabel() {
        return label;
    }

    public String getExplanation() {
        return explanation;
    }
}
