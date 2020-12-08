package ch.zhaw.it.pm3.unipoly;

import java.util.HashMap;
import java.util.Map;

public abstract class Owner implements Comparable<Owner> {

    private final int index;
    private final String name;
    private int money;
    private int modulesOwned;
    private int debt;
    private Owner debtor;
    private Map<Integer, FieldProperty> ownedModules;

    /***
     * owner constructor
     *
     * @param index        present owner index
     * @param id           present owner ID
     * @param initialMoney present owner intial money which is 200000
     */
    protected Owner(int index, String id, int initialMoney) {
        this.index = index;
        this.name = id;
        money = initialMoney;
        ownedModules = new HashMap<>();
        setModulsOwned();
    }

    /*------ GET functions ------------------------------------------*/
    public int getIndex() {
        return index;
    }

    public boolean isBank() {
        return this.index == -1;
    }

    public boolean isNPC() {
        return this.name.contains("NPC");
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public int getDebt() {
        return debt;
    }

    public int getModulesOwned() {
        return modulesOwned;
    }

    public Owner getDebtor() {
        return debtor;
    }

    public Map<Integer, FieldProperty> getownedModuls() {
        return ownedModules;
    }
    /*---------------------------------------------------------------*/

    public void setDebt(int amount) {
        this.debt = amount;
    }

    public void setDebtor(Owner debtor) {
        this.debtor = debtor;
    }

    public void setownedModuls(Map<Integer, FieldProperty> allModuls) {
        this.ownedModules = allModuls;
    }

    public void setModulsOwned() {
        this.modulesOwned = ownedModules.size();
    }

    public int getWealth() {
        int thisWealth = this.money;
        for (FieldProperty modul : this.ownedModules.values()) {
            thisWealth += modul.getCurrentRent();
        }
        return thisWealth;
    }

    public boolean setandgetBankrupt() {
        if (getWealth() < this.debt) {
            this.debtor.ownedModules.putAll(this.ownedModules);
            this.ownedModules.clear();
            this.debt -= getWealth();
            setModulsOwned();
            this.debtor.setModulsOwned();
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
     * @return the new amount of money
     */
    public void transferMoneyTo(Owner player, int amount) {
        player.money += amount;
        this.money -= amount;
    }

    /***
     * transfer properties between owners
     * 
     * @param newOwner   the new owner of the property
     * @param fieldIndex field index, of the property that needs to be transferred
     */
    public void transferPropertyTo(Owner newOwner, int fieldIndex) {
        this.ownedModules.get(fieldIndex).setOwnerIndex(newOwner.getIndex());
        newOwner.ownedModules.put(fieldIndex, this.ownedModules.get(fieldIndex));
        this.ownedModules.remove(fieldIndex);
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
        transferMoneyTo(owner, owner.ownedModules.get(fieldIndex).getPropertyCost());
        owner.transferPropertyTo(this, fieldIndex);
    }

    /***
     *  This method calculates what the Player owes
     * @param debtor is the dept
     * @param amount is the amount of dept
     * @return whether there is a debt or not
     */
    public boolean setAndCheckDebt(Owner debtor, int amount) {
        if (this.money < amount) {
            transferMoneyTo(debtor, this.money);
            this.debt = amount - this.money;
            this.debtor = debtor;
            return true;
        } else {
            transferMoneyTo(debtor, amount);
            this.debt = 0;
            this.debtor = null;
            return false;
        }
    }
}
