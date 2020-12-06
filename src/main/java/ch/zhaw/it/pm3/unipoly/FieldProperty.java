package ch.zhaw.it.pm3.unipoly;

import java.util.List;

/**
 * This class is for creating the property fields.
 */
public class FieldProperty extends Field{

    private final int propertyCost;
    private final String name;
    private final int rentLV1;
    private final int rentLV2;
    private final int rentLV3;
    private final int rentLV4;
    private final int rentLV5;
    private final int moduleGroupIndex;
    private int ECTS;
    private int currentRent;
    private int ownerIndex;
    private static final int UNOWNED = -1;

    /** Constructor for property field
     *
     * @param name of the property
     * @param label of the field, in this case PROPERTY
     * @param propertyCost is the cost of the property
     * @param rentLV1 the rent that you start with after acquiring the property
     * @param rentLV2 the rent after the property levels up once
     * @param rentLV3 the rent after the property levels up twice
     * @param rentLV4 the rent after the property levels up thrice
     * @param rentLV5 the rent after the property is leveled up to the maximum
     * @param moduleGroup is which the property belongs too
     */
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
        this.ECTS = 5 + moduleGroup;
    }

    /*------ GET functions ------------------------------------------*/
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
    public int getECTS() { return ECTS; }
    /*---------------------------------------------------------------*/

    public void setOwnerIndex(int ownerIndex) {
        this.ownerIndex = ownerIndex;
    }

    public boolean isOwnerBank() {
        return ownerIndex == -1;
    }

    /**This method levels up the rent of the property when called.**/
    public void raiseRentAndECTS() {
        if (currentRent != rentLV5) {
            ECTS += 2;
        }
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

}
