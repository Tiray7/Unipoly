package com.example.Softwareproject3;

import java.util.Map;

public class Board {
    private Map<Integer, Field> fields;

    public Board() {
        fields = Config.getInitialBoard();
    }

    public Config.FieldLabel getFieldTypeAtIndex(int index) throws FieldIndexException {
        if (index < Config.FIELD_MIN) {
            throw new FieldIndexException("Field index ist too small");
        } else if (index > Config.FIELD_MAX) {
            throw new FieldIndexException("Field index ist too large");
        }
        return fields.get(index).getLabel();
    }

    public String getFieldNameAtIndex(int index) throws FieldIndexException {
        if (index < Config.FIELD_MIN) {
            throw new FieldIndexException("Field index ist too small");
        } else if (index > Config.FIELD_MAX) {
            throw new FieldIndexException("Field index ist too large");
        }
        return fields.get(index).getName();
    }
}
