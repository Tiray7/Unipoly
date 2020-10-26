package com.example.Softwareproject3;

public class Bank extends Owner {

    // if you visit a fach which owend by another player
    private double freeParking = 100;

    private final int numberOfMoodle = 32;

    // initial money is 2000000
    public Bank() {
        super("Bank", 2000000);
    }

    public int getNumberOfMoodle(){
        return numberOfMoodle;
    }

    public double getFreeParking() {
        return freeParking;
    }

    // todo wait for tile class
    // public void payTax(Player player, Tile tile){

    //}

    public void awardFreeParking(Player player){
        this.transfer(player, freeParking);
        freeParking = 100;
    }
}
