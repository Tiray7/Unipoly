package com.example.Softwareproject3;

public class FieldProperty extends Field{

    private final int propertyCost;
    private final String name;
    private final int rentLV1;
    private final int rentLV2;
    private final int rentLV3;
    private final int rentLV4;
    private final int rentLV5;
    private int currentRent;
    private int ownerIndex;
    private static final int UNOWNED = -1;

    public FieldProperty(String name, Config.FieldLabel label, int propertyCost,
                         int rentLV1, int rentLV2, int rentLV3, int rentLV4, int rentLV5) {
        super(label);
        this.name = name;
        this.propertyCost = propertyCost;
        this.rentLV1 = rentLV1;
        this.rentLV2 = rentLV2;
        this.rentLV3 = rentLV3;
        this.rentLV4 = rentLV4;
        this.rentLV5 = rentLV5;
        this.currentRent = rentLV1;
        this.ownerIndex = UNOWNED;
    }

    public void setOwnerIndex(int ownerIndex) {
        this.ownerIndex = ownerIndex;
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

    public String getName() {
        return name;
    }

    public int getPropertyCost() {
        return propertyCost;
    }

    public int getOwnerIndex() {
        return ownerIndex;
    }

    public int getCurrentRent() {
        return currentRent;
    }
}
