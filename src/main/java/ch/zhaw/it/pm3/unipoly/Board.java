package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

@Repository
public class Board {
    private final Map<Integer, Field> fields;
    private final Map<Integer, FieldProperty> properties;
    private final Map<Integer, LinkedList<FieldProperty>> moduleGroups;

    /***
     * Board constructor
     */
    public Board() {
        fields = Config.getInitialBoard();
        properties = new HashMap<>();
        moduleGroups = new HashMap<>();
        fillPropertyMap();
        fillModuleGroups();
    }

    public Map<Integer, Field> getFields() { return fields; }
    public Map<Integer, FieldProperty> getProperties() { return properties; }
    public Map<Integer, LinkedList<FieldProperty>> getModuleGroups() { return moduleGroups; }
    
    /***
     * getFieldTypeAtIndex methode to find out the index label from teh index number
     * 
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
     * 
     * @param index field index
     * @return property in this field index
     * @throws FieldIndexException
     */
    public FieldProperty getFieldPropertyAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return properties.get(index);
    }

    public LinkedList<FieldProperty> getModuleGroupAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return moduleGroups.get(index);
    }

    public Integer getIndexFromField(FieldProperty property) {
        Integer index = null;
        for (Map.Entry<Integer, FieldProperty> entry : properties.entrySet()) {
            if (entry.getValue().equals(property)) {
                index = entry.getKey();
            }
        }
        return index;
    }

    /***
     * getFieldAtIndex methode to get what is the field on this index
     *
     * @param index field index
     * @return the field in the certain index
     * @throws FieldIndexException
     */
    public Field getFieldAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index);
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

    public int getPropertyOwner(int index) {
        return properties.get(index).getOwnerIndex();
    }

    /***
     * checkFieldIndex method to check the index and find if its right
     * 
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
    private void fillPropertyMap() {

        for (Map.Entry<Integer, Field> entry : fields.entrySet())
            if (entry.getValue().getLabel().equals(Config.FieldLabel.PROPERTY)) {
                properties.put(entry.getKey(), (FieldProperty) entry.getValue());
            }
    }

    private void fillModuleGroups() {
        for (int i = 0; i <= 7; i++) {
            moduleGroups.put(i, new LinkedList<>());
        }
        properties.forEach((integer, fieldProperty) -> {
            moduleGroups.get(fieldProperty.getModuleGroupIndex()).add(fieldProperty);
        });
    }

    public boolean checkOwnsModulGroup(FieldProperty currentProperty) {
        int moduleGroupIndex = currentProperty.getModuleGroupIndex();
        LinkedList<FieldProperty> currentModuleGroup = moduleGroups.get(moduleGroupIndex);
        int countSameOwner = 0;
        for (FieldProperty fieldOfSameModule : currentModuleGroup) {
            if (fieldOfSameModule.getOwnerIndex() == currentProperty.getOwnerIndex()) {
                countSameOwner++;
            }
        }
        if (countSameOwner == currentModuleGroup.size()) { 
            return true;
        }
        return false;
    }

    public void checkAndRaiseRentAndECTS(FieldProperty currentProperty) {
        if (checkOwnsModulGroup(currentProperty)) {
            raiseAll(currentProperty.getModuleGroupIndex());
        } else {
            currentProperty.raiseRentAndECTS();
        }
    }

    public void checkAndDecreaseRentAndECTS(FieldProperty currentProperty) {
        if (checkOwnsModulGroup(currentProperty)) {
            decreaseAll(currentProperty.getModuleGroupIndex());
        } else {
            currentProperty.decreaseRentAndECTS();
        }
    }

    public void raiseAll(int moduleGroupIndex) {
        moduleGroups.get(moduleGroupIndex).forEach(FieldProperty::raiseRentAndECTS);
    }

    public void decreaseAll(int moduleGroupIndex) {
        moduleGroups.get(moduleGroupIndex).forEach(FieldProperty::decreaseRentAndECTS);
    }

    public void resetLevelAll(int moduleGroupIndex) {
        moduleGroups.get(moduleGroupIndex).forEach(FieldProperty::resetLevel);
    }

}
