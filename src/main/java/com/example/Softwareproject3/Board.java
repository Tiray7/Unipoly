package com.example.Softwareproject3;

import java.util.Map;

public class Board {
    private Map<Integer, Field> fields;
    private Map<Integer, FieldProperty> properties;

    public Board() {
        fields = Config.getInitialBoard();
        fillPropertyMap();
    }

    public Config.FieldLabel getFieldTypeAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index).getLabel();
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

    private void checkFieldIndex(int index) throws FieldIndexException {
        if (index < Config.FIELD_MIN) {
            throw new FieldIndexException("Field index ist too small");
        } else if (index > Config.FIELD_MAX) {
            throw new FieldIndexException("Field index ist too large");
        }
    }

    private void fillPropertyMap() {
        for (Map.Entry<Integer, Field> entry : fields.entrySet())
            if (entry.getValue().getLabel().equals(Config.FieldLabel.PROPERTY)) {
                properties.put(entry.getKey(), (FieldProperty) entry.getValue());
            }
    }
}
