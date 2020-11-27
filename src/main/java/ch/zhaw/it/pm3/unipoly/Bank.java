package ch.zhaw.it.pm3.unipoly;

public class Bank extends Owner {

    // if you visit a fach which owend by another player
    private double freeParking = 100;
    private final int numberOfMoodle = 32;

    // initial money is 2000000
    public Bank() {
        super(-1, "Bank", 2000000);
    }

    public int getNumberOfMoodle(){
        return numberOfMoodle;
    }

    public double getFreeParking() {
        return freeParking;
    }

    public void awardFreeParking(Player player){
        this.transferMoneyTo(player, 100);
    }

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }
}
