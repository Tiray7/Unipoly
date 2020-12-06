package ch.zhaw.it.pm3.unipoly;

import java.util.HashMap;
import java.util.Map;

public abstract class Owner implements Comparable {

    private final int index;
    private final String name;
    private int money;
    private int ModulGroupOwned;
    private int PropertyOwned;
    private int Debt;
    private Owner Debtor;
    private Map<Integer, FieldProperty> ownedModuls;

    /***
     * owner constructor
     * @param index present owner index
     * @param id present owner ID
     * @param initialMoney present owner intial money which is 200000
     */
    public Owner(int index, String id, int initialMoney) {
        this.index = index;
        this.name = id;
        money = initialMoney;
        ownedModuls = new HashMap<Integer, FieldProperty>();
        setPropertyOwned();
    }

    public int getIndex() { return index; }
    public boolean isBank() { return this.index == -1; }
    public boolean isNPC() { return this.name.contains("NPC"); }
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getDebt() { return Debt; }
    public int getPropertyOwned() { return PropertyOwned; }
    public Owner getDebtor() { return Debtor; }
    public Map<Integer, FieldProperty> getownedModuls() { return ownedModuls; }
    public void setownedModuls(Map<Integer, FieldProperty> allModuls) { this.ownedModuls = allModuls; }
    public void setPropertyOwned() { this.PropertyOwned = ownedModuls.size(); }
    public void setDebtor(Owner debtor) {
        this.Debtor = debtor;
    }

    public int getWealth() { 
        int ThisWealth = this.money;
        for (FieldProperty modul : this.ownedModuls.values()) {
            ThisWealth += modul.getCurrentRent();
        }
        return ThisWealth;
    }

    public int compareTo(Owner comparply) {
        return comparply.getWealth() - this.getWealth();
    }

    // TODO: Call this function to check player owns ModulGroup
    public void setModulGroupOwned(int ModulGroupOwned) { this.ModulGroupOwned = ModulGroupOwned; }

    /***
     * transfer money between players only with certain amount
     * @param player which player is included with this transfer
     * @param amount what is the amount to transfer
     */
    public void transferMoneyTo(Owner player, int amount) {
        player.money += amount;
        this.money -= amount;
    }

    /***
     * transfer field bewteen player
     * @param owner present who will own the field
     * @param fieldIndex field index , which field
     */
    public void transferFieldTo(Owner owner, int fieldIndex) {
        this.ownedModuls.put(fieldIndex, owner.ownedModuls.get(fieldIndex));
        this.ownedModuls.get(fieldIndex).setOwnerIndex(this.index);
        owner.ownedModuls.remove(fieldIndex);
    }

    /***
     * buying property from another player
     * @param owner present the owner of the field
     * @param FieldIndex fild index which field
     */
    public void buyPropertyFrom(Owner owner, int FieldIndex) {
        transferMoneyTo(owner, owner.ownedModuls.get(FieldIndex).getPropertyCost());
        transferFieldTo(owner, FieldIndex);
        setPropertyOwned();
    }

    // TODO: Upgrade Property
    public void upgradeProperty(int FieldIndex) {
    }

    // TODO: Player landed on an owned field
    public boolean payRent(Owner ownerOfField, FieldProperty field) {
        return this.setandcheckDebt(ownerOfField, field.getCurrentRent());
        // Player has to pay Rent
        /*
         * amount = field.currentRent Player owner = Owner of the field.
         * 
         * then first check with setandcheckDebt() if he is able to pay if True:
         * Transfer the money if False: return false
         * 
         * if The Owner has whole modulgroup: increase rent of whole modulgroup else:
         * increse rent of this field return true
         */
    }

    // Calculate what the Player owes
    public boolean setandcheckDebt(Owner debtor, int amount) {
        if (this.money < amount) {
            transferMoneyTo(debtor, this.money);
            Debt = amount - this.money;
            Debtor = debtor;
            return true;
        } else {
            transferMoneyTo(debtor, amount);
            return false;
        }
    }
}
