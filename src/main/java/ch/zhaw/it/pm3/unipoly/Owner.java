package ch.zhaw.it.pm3.unipoly;

import java.util.ArrayList;

public abstract class Owner {

    private final int index;
    private final String name;
    private int money;
    private int RoadOwned;
    private int PropertyOwned;
    private int Debt;
    private Owner Debtor;
    private ArrayList<FieldProperty> ownedModuls;

    public Owner(int index, String id, int initialMoney) {
        this.index = index;
        this.name = id;
        money = initialMoney;
        ownedModuls = new ArrayList<FieldProperty>();
    }

    public int getIndex() { return index; }
    public boolean isBank() { return this.index == -1; }
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getRoadOwned() { return RoadOwned; }
    public int getDebt() { return Debt; }
    public Owner getDebtor() { return Debtor; }
    public ArrayList<FieldProperty> getownedModuls() { return ownedModuls; }

    public int setandgetPropertyOwned() {  
        this.PropertyOwned = ownedModuls.size(); 
        return PropertyOwned;
    }

    // TODO: Call this function to check player own the road
    // or maybe a function in fields
    public void setRoadOwned(int roadOwned) { this.RoadOwned = roadOwned; }


    // Player has to pay a certain amount to another Player/Bank
    public void transferMoneyTo(Owner player, int amount) {
        player.money += amount;
        this.money -= amount;
    }

     // TODO: transfer a Field
     public void transferFieldTo(Owner owner, FieldProperty field) {
        if(!owner.isBank()){ 
            this.ownedModuls.add(field);
            owner.ownedModuls.remove(field); 
        }
        field.setOwnerIndex(this.index);
    }

    // TODO: Player landed on an owned field
    public boolean payRent(FieldProperty field) {
        // Player has to pay Rent
        /*
        amount = field.currentRent
        Player owner = Owner of the field.
        If the currentPlayer(this) is not the Owner
            then first check with setandcheckDebt() if he is able to pay
        if True:
                Transfer the money
        if False:
                return false

        if The Owner has whole modulgroup:
                increase rent of whole modulgroup
            else:
                increse rent of this field
            return true
        */
        return true;
    }

    // TODO: Calculate what the Player owes
    public boolean setandcheckDebt(Owner debtor, int amount) {
        /*
        if (currentplayer cant pay amount) {
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

    // TODO: buy a Property
    public void buyPropertyFrom(Owner owner, FieldProperty field) {
        transferMoneyTo(owner, field.getPropertyCost());
        transferFieldTo(owner, field);
    }
}
