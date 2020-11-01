package com.example.Softwareproject3;

import java.util.*;

public abstract class Owner {

    int index;
    private String name;
    private double money;
    private int RoadOwned;
    private int PropertyOwned;

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


// todo make trnasfer mony to buy property
    //public double transfer(Owner recipient, Tile tile, double price) {

//}

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
}