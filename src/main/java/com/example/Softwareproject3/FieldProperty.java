package com.example.Softwareproject3;

public class FieldProperty extends Field{

    private int propertyCost;
    private int rentLV1;
    private int rentLV2;
    private int rentLV3;
    private int rentLV4;
    private int rentLV5;
    private int ownerIndex;
    private int amountHouses;
    private static final int UNOWNED =-1;

    public FieldProperty(String name, Config.FieldLabel label, int propertyCost, int rentLV1, int rentLV2, int rentLV3, int rentLV4, int rentLV5) {
        super(name, label);
        this.propertyCost = propertyCost;
        this.rentLV1 = rentLV1;
        this.rentLV2 = rentLV2;
        this.rentLV3 = rentLV3;
        this.rentLV4 = rentLV4;
        this.rentLV5 = rentLV5;

    }

    public void setOwnerIndex(int ownerIndex) {
        this.ownerIndex = ownerIndex;
    }

    public void setAmountHouses(int amountHouses) {
        this.amountHouses = amountHouses;
    }

    public int getPropertyCost() {
        return propertyCost;
    }

    public int getRentLV1() {
        return rentLV1;
    }

    public int getRentLV2() {
        return rentLV2;
    }

    public int getRentLV3() {
        return rentLV3;
    }

    public int getRentLV4() {
        return rentLV4;
    }

    public int getOwnerIndex() {
        return ownerIndex;
    }

    public int getAmountHouses() {
        return amountHouses;
    }
}
