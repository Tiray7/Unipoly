package ch.zhaw.it.pm3.unipoly;
/**
 * This class is for creating the following simple fields
 * Go, Visit, Recess, Detention, Chance and Jump.
 **/

public class Field {

    public Integer getLabel;
    private Config.FieldLabel label;
    private String explanation;

    /** Constructer for Field
     *
     * @param label is the type of the field
     * @param explanation for what the field does
     */
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
