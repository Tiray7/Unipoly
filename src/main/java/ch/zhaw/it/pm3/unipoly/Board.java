package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class initialises the board with all the fields and allows the manipulation of rent and ECTS-points.
 */

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

    public Map<Integer, Field> getFields() {
        return fields;
    }

    public Map<Integer, FieldProperty> getProperties() {
        return properties;
    }

    public Map<Integer, LinkedList<FieldProperty>> getModuleGroups() {
        return moduleGroups;
    }

    /***
     * This method returns the label of a normal field.
     *
     * @param index of the field
     * @return label of the field
     * @throws FieldIndexException if the index is out of bounds
     */
    public Config.FieldLabel getFieldTypeAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index).getLabel();
    }

    /***
     * Returns the label of a property field.
     *
     * @param index of the field
     * @return FieldProperty at the given index
     * @throws FieldIndexException if the index is out of bounds
     */
    public FieldProperty getFieldPropertyAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return properties.get(index);
    }

    /**
     * Find the fields of the same modulegroup to which the given fieldIndex belongs to.
     *
     * @param index of the field in question
     * @return list of fields from the same modulegroup
     * @throws FieldIndexException if the index is out of range
     */
    public LinkedList<FieldProperty> getModuleGroupAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return moduleGroups.get(index);
    }

    /**
     * Find the index of a given FieldProperty.
     *
     * @param property in question
     * @return the index belonging to the property
     */
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
     * getFieldAtIndex method to get the field at this index
     *
     * @param index of the field
     * @return the field at the given index
     * @throws FieldIndexException if the index is invalid
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
     * This method checks if the given index is on the board. If not then an exception is thrown.
     *
     * @param index of field
     * @throws FieldIndexException if the index is out of bounds of the board
     */
    private void checkFieldIndex(int index) throws FieldIndexException {
        if (index < Config.FIELD_MIN) {
            throw new FieldIndexException("Field index ist too small");
        } else if (index > Config.FIELD_MAX) {
            throw new FieldIndexException("Field index ist too large");
        }
    }

    /***
     * This method fills the propertyMap with all properties and their indexes.
     */
    private void fillPropertyMap() {
        for (Map.Entry<Integer, Field> entry : fields.entrySet())
            if (entry.getValue().getLabel().equals(Config.FieldLabel.PROPERTY)) {
                properties.put(entry.getKey(), (FieldProperty) entry.getValue());
            }
    }

    /***
     * This method fills the mouduleMap with the grouped properties.
     */
    private void fillModuleGroups() {
        for (int i = 0; i <= 7; i++) {
            moduleGroups.put(i, new LinkedList<>());
        }
        properties.forEach((integer, fieldProperty) -> moduleGroups.get(fieldProperty.getModuleGroupIndex()).add(fieldProperty));
    }

    /**
     * Checks if a player owns the properties of a whole moduleGroup.
     *
     * @param currentProperty modulegroup in question
     * @return whether he owns the modulegroup or not
     */
    public boolean checkOwnsModuleGroup(FieldProperty currentProperty) {
        int moduleGroupIndex = currentProperty.getModuleGroupIndex();
        LinkedList<FieldProperty> currentModuleGroup = moduleGroups.get(moduleGroupIndex);
        int countSameOwner = 0;
        for (FieldProperty fieldOfSameModule : currentModuleGroup) {
            if (fieldOfSameModule.getOwnerIndex() == currentProperty.getOwnerIndex()) {
                countSameOwner++;
            }
        }
        return countSameOwner == currentModuleGroup.size();
    }

    /**
     * Raise the rent and the reward of ECTS-points of a modulegroup.
     *
     * @param currentProperty modulegroup in question
     */
    public void checkAndRaiseRentAndECTS(FieldProperty currentProperty) {
        if (checkOwnsModuleGroup(currentProperty)) {
            raiseAll(currentProperty.getModuleGroupIndex());
        } else {
            currentProperty.raiseRentAndECTS();
        }
    }

    /**
     * Decrease the rent and the reward of ECTS-points of a modulegroup.
     *
     * @param currentProperty modulegroup in question
     */
    public void checkAndDecreaseRentAndECTS(FieldProperty currentProperty) {
        if (checkOwnsModuleGroup(currentProperty)) {
            decreaseAll(currentProperty.getModuleGroupIndex());
        } else {
            currentProperty.decreaseRentAndECTS();
        }
    }

    /**
     * Raise the rent of a modulegroup.
     *
     * @param moduleGroupIndex in question
     */
    public void raiseAll(int moduleGroupIndex) {
        moduleGroups.get(moduleGroupIndex).forEach(FieldProperty::raiseRentAndECTS);
    }

    /**
     * Decrease the rent of a modulegroup.
     *
     * @param moduleGroupIndex in question
     */
    public void decreaseAll(int moduleGroupIndex) {
        moduleGroups.get(moduleGroupIndex).forEach(FieldProperty::decreaseRentAndECTS);
    }

    /**
     * Reset the rent of a modulegroup.
     *
     * @param moduleGroupIndex in question
     */
    public void resetLevelAll(int moduleGroupIndex) {
        moduleGroups.get(moduleGroupIndex).forEach(FieldProperty::resetLevel);
    }

}
