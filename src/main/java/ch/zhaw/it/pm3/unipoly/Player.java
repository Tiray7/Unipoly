package ch.zhaw.it.pm3.unipoly;

public class Player extends Owner {

    private Token token;
    private boolean bankrupt = false;
    private boolean FreeCard = false;
    private int leftTimeInDetention = 0;

    /***
     * constructor Player
     * @param index present player index
     * @param name present player name
     * @param tokenType present what he has for token
     */
    public Player(int index, String name, TokenType tokenType) {
        super(index, name, 300);
        token = new Token(tokenType);
    }

    public boolean getFreeCard() {
        return FreeCard;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setFreeCard(boolean outDetention) {
        FreeCard = outDetention;
    }

    public boolean getBankrupt() {
        return bankrupt;
    }

    public void setBankrupt(boolean isBankrupt) {
        this.bankrupt = isBankrupt;
    }

    public int getleftTimeInDetention() {
        return leftTimeInDetention;
    }

    public void decreaseleftTimeInDetention() {
        this.leftTimeInDetention--;
    }
    
    public boolean inDetention() {
        return leftTimeInDetention > 0;
    }

    public void goDetention() {
        token.moveTo(9);
        this.leftTimeInDetention = 3;
    }

    public void outDetention() {
        this.leftTimeInDetention = 0;
    }
}
