package ch.zhaw.it.pm3.unipoly;

import java.util.List;

public class FieldProperty extends Field{

    private final int propertyCost;
    private final String name;
    private final int rentLV1;
    private final int rentLV2;
    private final int rentLV3;
    private final int rentLV4;
    private final int rentLV5;
    private final int moduleGroupIndex;
    private int currentRent;
    private int ownerIndex;
    private static final int UNOWNED = -1;
    private List<FieldProperty> moduleGroup;

    public FieldProperty(String name, Config.FieldLabel label, int propertyCost,
                         int rentLV1, int rentLV2, int rentLV3, int rentLV4, int rentLV5, int moduleGroup) {
        super(label, "");
        this.name = name;
        this.propertyCost = propertyCost;
        this.rentLV1 = rentLV1;
        this.rentLV2 = rentLV2;
        this.rentLV3 = rentLV3;
        this.rentLV4 = rentLV4;
        this.rentLV5 = rentLV5;
        this.moduleGroupIndex = moduleGroup;
        this.currentRent = rentLV1;
        this.ownerIndex = UNOWNED;
    }

    public void initializeModuleGroupe(List<FieldProperty> moduleGroupe){
        this.moduleGroup = moduleGroupe;
    }

    public String getName() { return name; }
    public int getPropertyCost() { return propertyCost; }
    public int getOwnerIndex() { return ownerIndex; }
    public int getCurrentRent() { return currentRent; }
    public int getRentLV1() { return rentLV1; }
    public int getRentLV2() { return rentLV2; }
    public int getRentLV3() { return rentLV3; }
    public int getRentLV4() { return rentLV4; }
    public int getRentLV5() { return rentLV5; }
    public int getModuleGroupIndex(){ return moduleGroupIndex; }
    public List<FieldProperty> getModuleGroup() { return moduleGroup; }

    public void setOwnerIndex(int ownerIndex) {
        this.ownerIndex = ownerIndex;
    }

    public boolean isOwnerBank() {
       return ownerIndex == -1;
    }

    public void raiseRent() {
        if (currentRent == rentLV1) {
            currentRent = rentLV2;
        } else if (currentRent == rentLV2) {
            currentRent = rentLV3;

        } else if (currentRent == rentLV3) {
            currentRent = rentLV4;

        } else if (currentRent == rentLV4) {
            currentRent = rentLV5;
        }
    }

    public void raiseRentAll(){
       for (FieldProperty fieldOfSameModule : moduleGroup) {
            fieldOfSameModule.raiseRent();
        }
    }

    public void checkAndRaiseRent(){
        int countOwnedModules = 0;
        raiseRentAll();
        for (FieldProperty fieldOfSameModule : moduleGroup) {
            if(this.moduleGroupIndex==fieldOfSameModule.moduleGroupIndex){
                countOwnedModules++;
            }
        }
        if(this.moduleGroupIndex==0||this.moduleGroupIndex==7) {
            if (countOwnedModules == 2)
                raiseRentAll();
        } else {
            if(countOwnedModules == 3)
                raiseRentAll();
        }
    }




}
