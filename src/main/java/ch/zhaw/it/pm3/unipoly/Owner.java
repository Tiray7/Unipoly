package ch.zhaw.it.pm3.unipoly;

import java.util.HashMap;
import java.util.Map;

public abstract class Owner {

    int index;
    private final String name;
    private int money;
    private int RoadOwned;
    private int PropertyOwned;
    private int Debt;
    private Owner Debtor;
    private Map<Integer, FieldProperty> ownedModuls;

    public Owner(String id, int initialMoney) {
        this.name = id;
        money = initialMoney;
        ownedModuls = new HashMap<>();
    }

    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getRoadOwned() { return RoadOwned; }
    public int getDebt() { return Debt; }
    public Owner getDebtor() { return Debtor; }
    public Map<Integer, FieldProperty> getownedModuls() { return ownedModuls; }

    public int setandgetPropertyOwned() {  
        this.PropertyOwned = ownedModuls.size(); 
        return PropertyOwned;
    }

    // TODO: !
    public void setRoadOwned(int roadOwned) { this.RoadOwned = roadOwned; }

    /*
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
    */

    // TODO: Player has to pay a certain amount to another Player/Bank
    public void transferMoneyTo(Owner player, int amount) {
        player.money += amount;
        this.money -= amount;
    }

    // TODO: Player landed on an owned field
    public boolean payRent(FieldProperty field) {
        Owner ownerOfField = field.getPlayers().get(field.getOwnerIndex());
        this.transferMoneyTo(ownerOfField, field.getCurrentRent());
        field.raiseRent();
        return true;
    }

    // TODO: Calculate what the Player owes
    public boolean setandcheckDebt(Owner debtor, int amount) {
/*
        if (this.getMoney()<amount) {
            

            Pay what you have to Debtor
            set Debt
            set Debtor
            return true;
        } else {
            transferMoneyTo(debtor, amount);
            return false;


        }
*/
        return false;
    }
}
