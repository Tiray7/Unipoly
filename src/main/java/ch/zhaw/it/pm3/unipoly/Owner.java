package ch.zhaw.it.pm3.unipoly;

import java.util.ArrayList;

public abstract class Owner {

    int index;
    private final String name;
    private double money;
    private int RoadOwned;
    private int PropertyOwned;
    private ArrayList<Integer> deedIndices;

    public Owner(String id, double initialMoney) {
        this.name = id;
        money = initialMoney;
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }



    public double transfer(Owner peoperty, Field tile, double price) {
        if (!(this instanceof Bank)) {
            int i = this.getDeeds().indexOf(tile.getLabel());
            this.getDeeds().remove(i);
        }
        peoperty.getDeeds().add(tile.getLabel);
        tile.setOwnerIndex(peoperty.index);


        if(tile.getLabel == tile.getLabel) {
            int numOwned = peoperty.PropertyOwned;
            peoperty.setPropertyOwned(numOwned + 1);

            if(this.name != "Bank")
            {
                numOwned = this.getPropertyOwned();
                this.setPropertyOwned(numOwned - 1);
            }

        }

        return peoperty.transfer(this, price);
    }


    public double transfer(Owner property, Double amount) {
        double newBalance = money - amount;
        if (newBalance < 0) newBalance = 0;
        double transfer = money - newBalance;
        property.money += transfer;
        money -= transfer;
        return transfer;
    }

    public int getRoadOwned() {
        return RoadOwned;
    }

    public int getPropertyOwned() {
        return PropertyOwned;
    }

    public void setPropertyOwned(int numUtilitiesOwned) {
        this.PropertyOwned = numUtilitiesOwned;
    }

    public void setRoadOwned(int roadOwned) {
        this.RoadOwned = roadOwned;
    }
    public ArrayList<Integer> getDeeds() {
        return deedIndices;
    }
}