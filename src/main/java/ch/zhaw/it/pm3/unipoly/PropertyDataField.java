package ch.zhaw.it.pm3.unipoly;

/**
 * This class is an argument object for FieldProperty.
 */
public class PropertyDataField {

    private Config.FieldLabel label;
    private int propertyCost;
    private int rentLV1;
    private int rentLV2;
    private int rentLV3;
    private int rentLV4;
    private int rentLV5;
    private int moduleGroupIndex;

    /**
     * Constructor with the required parameters for FieldProperty
     *
     * @param name             of the property
     * @param label            of the field, in this case PROPERTY
     * @param propertyCost     is the cost of the property
     * @param rentLV1          the rent that you start with after acquiring the property
     * @param rentLV2          the rent after the property levels up once
     * @param rentLV3          the rent after the property levels up twice
     * @param rentLV4          the rent after the property levels up thrice
     * @param rentLV5          the rent after the property is leveled up to the maximum
     * @param moduleGroupIndex is which the property belongs too
     */
    public PropertyDataField(String name, Config.FieldLabel label, int propertyCost, int rentLV1, int rentLV2, int rentLV3, int rentLV4, int rentLV5, int moduleGroupIndex) {
        this.name = name;
        this.label = label;
        this.propertyCost = propertyCost;
        this.rentLV1 = rentLV1;
        this.rentLV2 = rentLV2;
        this.rentLV3 = rentLV3;
        this.rentLV4 = rentLV4;
        this.rentLV5 = rentLV5;
        this.moduleGroupIndex = moduleGroupIndex;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Config.FieldLabel getLabel() {
        return label;
    }

    public void setLabel(Config.FieldLabel label) {
        this.label = label;
    }

    public int getPropertyCost() {
        return propertyCost;
    }

    public void setPropertyCost(int propertyCost) {
        this.propertyCost = propertyCost;
    }

    public int getRentLV1() {
        return rentLV1;
    }

    public void setRentLV1(int rentLV1) {
        this.rentLV1 = rentLV1;
    }

    public int getRentLV2() {
        return rentLV2;
    }

    public void setRentLV2(int rentLV2) {
        this.rentLV2 = rentLV2;
    }

    public int getRentLV3() {
        return rentLV3;
    }

    public void setRentLV3(int rentLV3) {
        this.rentLV3 = rentLV3;
    }

    public int getRentLV4() {
        return rentLV4;
    }

    public void setRentLV4(int rentLV4) {
        this.rentLV4 = rentLV4;
    }

    public int getRentLV5() {
        return rentLV5;
    }

    public void setRentLV5(int rentLV5) {
        this.rentLV5 = rentLV5;
    }

    public int getModuleGroupIndex() {
        return moduleGroupIndex;
    }

    public void setModuleGroupIndex(int moduleGroupIndex) {
        this.moduleGroupIndex = moduleGroupIndex;
    }


}
