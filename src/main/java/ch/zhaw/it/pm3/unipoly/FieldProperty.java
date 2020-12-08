package ch.zhaw.it.pm3.unipoly;

public class FieldProperty extends Field {

    private final int propertyCost;
    private final String name;
    private final int rentLV1;
    private final int rentLV2;
    private final int rentLV3;
    private final int rentLV4;
    private final int rentLV5;
    private final int moduleGroupIndex;
    private int currentECTSLevel;
    private int currentRent;
    private int ownerIndex;
    public static final int UNOWNED = -1;

    /**
     * Constructor for property field
     */
    public FieldProperty(PropertyDataField dataFields) {
        super(dataFields.getLabel(), "");
        this.name = dataFields.getName();
        this.propertyCost = dataFields.getPropertyCost();
        this.rentLV1 = dataFields.getRentLV1();
        this.rentLV2 = dataFields.getRentLV2();
        this.rentLV3 = dataFields.getRentLV3();
        this.rentLV4 = dataFields.getRentLV4();
        this.rentLV5 = dataFields.getRentLV5();
        this.moduleGroupIndex = dataFields.getModuleGroupIndex();
        this.currentRent = rentLV1;
        this.ownerIndex = UNOWNED;
        //This sets the start ECTS points for the modules, we defined them to be 10 + 2 * moduleGroupIndex
        this.currentECTSLevel = 10 + 2 * moduleGroupIndex;
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
    public int getCurrentECTSLevel() { return currentECTSLevel; }
    /*---------------------------------------------------------------*/

    public void setOwnerIndex(int ownerIndex) {
        this.ownerIndex = ownerIndex;
    }

    public void setCurrentRent(int currentRent) {
        this.currentRent = currentRent;
    }

    public boolean isOwnerBank() {
        return ownerIndex == -1;
    }

    /** This method levels up the rent of the property when called. **/
    public void raiseRentAndECTS() {
        int ectsPointsChange = 4;
        if (currentRent != rentLV5) {
            currentECTSLevel += ectsPointsChange;
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

    public void decreaseRentAndECTS() {
        int ectsPointsChange = -4;
        if (currentRent != rentLV1) {
            currentECTSLevel -= ectsPointsChange;
        }
        if (currentRent == rentLV2) {
            currentRent = rentLV1;
        } else if (currentRent == rentLV3) {
            currentRent = rentLV2;
        } else if (currentRent == rentLV4) {
            currentRent = rentLV3;
        } else if (currentRent == rentLV5) {
            currentRent = rentLV4;
        }
    }

    public void resetLevel() {
        //this will return the point to the start ECTS level
        currentECTSLevel = 10 + 2*moduleGroupIndex;
        currentRent = rentLV1;
    }

}
