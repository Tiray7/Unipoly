package ch.zhaw.it.pm3.unipoly;

import java.util.HashMap;
import java.util.Map;

public abstract class Owner implements Comparable<Owner> {

    private final int index;
    private final String name;
    private int money;
    private int ModulsOwned;
    private int Debt;
    private Owner Debtor;
    private Map<Integer, FieldProperty> ownedModuls;

    /***
     * owner constructor
     * 
     * @param index        present owner index
     * @param id           present owner ID
     * @param initialMoney present owner intial money which is 200000
     */
    public Owner(int index, String id, int initialMoney) {
        this.index = index;
        this.name = id;
        money = initialMoney;
        ownedModuls = new HashMap<Integer, FieldProperty>();
        setModulsOwned();
    }

    /*------ GET functions ------------------------------------------*/
    public int getIndex() { return index; }
    public boolean isBank() { return this.index == -1; }
    public boolean isNPC() { return this.name.contains("NPC"); }
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getDebt() { return Debt; }
    public int getModulsOwned() { return ModulsOwned; }
    public Owner getDebtor() { return Debtor; }
    public Map<Integer, FieldProperty> getownedModuls() { return ownedModuls; }
    /*---------------------------------------------------------------*/
    
    public void setDebt(int amount){ this.Debt = amount; }
    public void setDebtor(Owner debtor) { this.Debtor = debtor; }
    public void setownedModuls(Map<Integer, FieldProperty> allModuls) { this.ownedModuls = allModuls; }
    public void setModulsOwned() { this.ModulsOwned = ownedModuls.size(); }

    public int getWealth() {
        int ThisWealth = this.money;
        for (FieldProperty modul : this.ownedModuls.values()) {
            ThisWealth += modul.getCurrentRent();
        }
        return ThisWealth;
    }

    public boolean setandgetBankrupt() {
        if (getWealth() < this.Debt) {
            this.Debtor.ownedModuls.putAll(this.ownedModuls);
            this.ownedModuls.clear();
            this.Debt -= getWealth();
            setModulsOwned();
            this.Debtor.setModulsOwned();
            return true;
        }
        return false;
    }

    public int compareTo(Owner comparable) {
        return comparable.getWealth() - this.getWealth();
    }

    /***
     * transfer money between players only with certain amount
     *  @param player which player is included with this transfer
     * @param amount what is the amount to transfer
     * @return
     */
    public double transferMoneyTo(Owner player, int amount) {
        player.money += amount;
        this.money -= amount;
        return this.money;
    }

    /***
     * transfer properties between owners
     * 
     * @param newOwner   the new owner of the property
     * @param fieldIndex field index, of the property that needs to be transferred
     */
    public void transferPropertyTo(Owner newOwner, int fieldIndex) {
        this.ownedModuls.get(fieldIndex).setOwnerIndex(newOwner.getIndex());
        newOwner.ownedModuls.put(fieldIndex, this.ownedModuls.get(fieldIndex));
        this.ownedModuls.remove(fieldIndex);
        setModulsOwned();
        newOwner.setModulsOwned();
    }

    /***
     * buy a property from another owner
     * 
     * @param owner      is the current owner of the field
     * @param fieldIndex field index of the property to be sold
     */
    public void buyPropertyFrom(Owner owner, int fieldIndex) {
        transferMoneyTo(owner, owner.ownedModuls.get(fieldIndex).getPropertyCost());
        owner.transferPropertyTo(this, fieldIndex);
    }

    /***
     *  setandcheckDebt methode Calculate what the Player owes
     * @param debtor is the dept
     * @param amount is the amount of dept
     * @return
     */
    public boolean setandcheckDebt(Owner debtor, int amount) {
        if (this.money < amount) {
            transferMoneyTo(debtor, this.money);
            this.Debt = amount - this.money;
            this.Debtor = debtor;
            return true;
        } else {
            transferMoneyTo(debtor, amount);
            this.Debt = 0;
            this.Debtor = null;
            return false;
        }
    }
}
