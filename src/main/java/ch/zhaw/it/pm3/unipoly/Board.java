package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class Board {
    private final Map<Integer, Field> fields;
    private final Map<Integer, FieldProperty> properties;

    public Board() {
        fields = Config.getInitialBoard();
        properties = new HashMap<>();
        fillPropertyMap();
        fillModuleGroupeMap();
    }

    public Config.FieldLabel getFieldTypeAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return fields.get(index).getLabel();
    }

    public FieldProperty getFieldPropertyAtIndex(int index) throws FieldIndexException {
        checkFieldIndex(index);
        return properties.get(index);
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

    private void fillModuleGroupeMap() {
        Map<Integer, List< FieldProperty>> moduleGroupeMaps = new HashMap<Integer, List<FieldProperty>>();
        for(int i = 0; i<=7; i++){
            moduleGroupeMaps.put(i,new LinkedList<FieldProperty>());
        }
        properties.forEach((integer, fieldProperty) -> {
            moduleGroupeMaps.get(fieldProperty.getModuleGroupIndex()).add(fieldProperty);
        });
        properties.forEach((integer, fieldProperty) -> {

           fieldProperty.initializeModuleGroupe( moduleGroupeMaps.get(fieldProperty.getModuleGroupIndex()));
        });
    }

    /*
    public boolean moduleGroupOfFieldIsOwned(FieldProperty currentField) {
       int currentModule =  currentField.getModuleGroupIndex();
       AtomicInteger ownedModules = new AtomicInteger();
       int NO_OWNER = -1;
       boolean output = false;
      properties.forEach((integer, fieldProperty) -> {
                if (fieldProperty.getModuleGroupIndex()==currentModule)
                    if (fieldProperty.getOwnerIndex()!=NO_OWNER)
                        ownedModules.getAndIncrement();
              }
              );

           if (currentModule ==0||currentModule==7) {
               if (ownedModules.equals(2))
                   output = true;
           } else {
               if (ownedModules.equals(3))
                   output = true;
           }
           return output;
    }*/
}
