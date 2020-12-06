package ch.zhaw.it.pm3.unipoly;



public class Bank extends Owner {

    /***
     * free parking award is 100
     * number of moodle is 32
     */
    private double freeParking = 100;
    private final int numberOfMoodle = 32;


    /***
     * initial  money is 2000000
     */
    public Bank() {
        super(-1, "Bank", 2000000);
    }

    /***
     *  get number of moodle
     * @return number of moodle
     */
    public int getNumberOfMoodle(){ return numberOfMoodle; }

    /***
     * get free parking award
     * @return free parking
     */
    public double getFreeParking() {
        return freeParking;
    }

    /***
     * give award for free parking with 100
     * @param player
     */
    public void awardFreeParking(Player player){
        this.transferMoneyTo(player, 100);
    }

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }
}
