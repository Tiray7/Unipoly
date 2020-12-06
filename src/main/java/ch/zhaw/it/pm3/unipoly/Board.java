package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class Board {
    private final Map<Integer, Field> fields;
    private final Map<Integer, FieldProperty> properties;

    /***
     * Board contractor
     */
    public Board() {
        fields = Config.getInitialBoard();
        properties = new HashMap<>();
        fillPropertyMap();
    }

    /***
     * getFieldTypeAtIndex methode to find out the index label from teh index number
     * @param index field index
     * @return index and label of the field
     * @throws FieldIndexException
     */
    public Config.FieldLabel getFieldTypeAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index).getLabel();
    }

    /***
     * getFieldPropertyAtIndex method to get which property in this field index
     * @param index field index
     * @return property in this field index
     * @throws FieldIndexException
     */
    public FieldProperty getFieldPropertyAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return properties.get(index);
    }

    /***
     * getFieldAtIndex methode to get what is the field on this index
     * @param index field index
     * @return the field in the certain index
     * @throws FieldIndexException
     */
    public Field getFieldAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index);
    }

    public Map<Integer, Field> getFields() {
        return fields;
    }

    public Map<Integer, FieldProperty> getProperties() {
        return properties;
    }

    public String getPropertyNameAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return properties.get(index).getName();
    }

    public int getRentFromProperty(int index) {
        return properties.get(index).getCurrentRent();
    }

    public int getCostFromProperty(int index) {
        return properties.get(index).getPropertyCost();
    }

    public void raiseRentFromProperty(int index) {
        properties.get(index).raiseRent();
    }

    public int getPropertyOwner(int index) {
        return properties.get(index).getOwnerIndex();
    }

    /***
     * checkFieldIndex method to check the index
     * and find if its right
     * @param index field index
     * @throws FieldIndexException
     */
    private void checkFieldIndex(int index) throws FieldIndexException {
        if (index < Config.FIELD_MIN) {
            throw new FieldIndexException("Field index ist too small");
        } else if (index > Config.FIELD_MAX) {
            throw new FieldIndexException("Field index ist too large");
        }
    }

    /***
     * fillPropertyMap method , void method to fill field property field
     */
    void fillPropertyMap() {
        for (Map.Entry<Integer, Field> entry : fields.entrySet())
            if (entry.getValue().getLabel().equals(Config.FieldLabel.PROPERTY)) {
                properties.put(entry.getKey(), (FieldProperty) entry.getValue());
            }
    }
}
