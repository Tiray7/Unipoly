package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class Board {
    private final Map<Integer, Field> fields;
    private final Map<Integer, FieldProperty> properties;
    private final Map<Integer, LinkedList<FieldProperty>> moduleGroups;

    public Board() {
        fields = Config.getInitialBoard();
        properties = new HashMap<>();
        moduleGroups = new HashMap<>();
        fillPropertyMap();
        fillModuleGroups();
    }

    public Config.FieldLabel getFieldTypeAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index).getLabel();
    }

    public FieldProperty getFieldPropertyAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return properties.get(index);
    }

    public LinkedList<FieldProperty> getModuleGroupAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return moduleGroups.get(index);
    }

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

    public Map<Integer, LinkedList<FieldProperty>> getModuleGroups() {
        return moduleGroups;
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

    private void fillModuleGroups() {
        for(int i = 0; i<=7; i++){
            moduleGroups.put(i, new LinkedList<>());
        }
        properties.forEach((integer, fieldProperty) -> {
            moduleGroups.get(fieldProperty.getModuleGroupIndex()).add(fieldProperty);
        });
    }



    public void raiseRentAll(int moduleGroupIndex){
        for (FieldProperty fieldOfSameModule : moduleGroups.get(moduleGroupIndex)) {
            fieldOfSameModule.raiseRent();
        }
    }
    public void checkAndRaiseRent(FieldProperty currentProperty){
        int moduleGroupIndex = currentProperty.getModuleGroupIndex();
        LinkedList<FieldProperty> currentModuleGroup = moduleGroups.get(moduleGroupIndex);

        int countOwnedModules = 0;
        int countSameOwner = 0;
        int NO_OWNER = -1;
        for (FieldProperty fieldOfSameModule : currentModuleGroup) {
            if(fieldOfSameModule.getOwnerIndex()!=NO_OWNER){
                countOwnedModules++;
            }
            if(currentProperty.getOwnerIndex()==fieldOfSameModule.getOwnerIndex()){
                countSameOwner++;
            }
        }

        if(countOwnedModules==currentModuleGroup.size()){
            raiseRentAll(moduleGroupIndex);
            if(countSameOwner==currentModuleGroup.size())
                raiseRentAll(moduleGroupIndex);
        } else {
            currentProperty.raiseRent();
        }
    }
}
