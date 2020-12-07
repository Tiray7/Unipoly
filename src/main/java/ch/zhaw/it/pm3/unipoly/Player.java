package ch.zhaw.it.pm3.unipoly;

public class Player extends Owner {

    private Token token;
    private boolean FreeCard = false;
    private int leftTimeInDetention = 0;
    private int ects = 0;

    /***
     * constructor Player
     * @param index is the player index
     * @param name is the player name
     * @param tokenType is the token type
     */

    public Player(int index, String name, Token.TokenType tokenType) {
        super(index, name, 1500);
        token = new Token(tokenType);
    }

    /*------ GET functions ------------------------------------------*/
    public boolean getFreeCard() { return FreeCard; }
    public Token getToken() { return token; }
    public int getECTS() { return ects; }
    public int getleftTimeInDetention() { return leftTimeInDetention; }
    /*---------------------------------------------------------------*/
    
    public void setToken(Token token) {
        this.token = token;
    }

    public void setFreeCard(boolean outDetention) {
        FreeCard = outDetention;
    }

    public void increaseECTS(int amount) {
        this.ects += amount;
    }

    public boolean isBachelor(){ 
        return ects >= 180;
    }

    public void decreaseleftTimeInDetention() {
        this.leftTimeInDetention--;
    }
    
    public boolean inDetention() {
        return leftTimeInDetention > 0;
    }

    public void setLeftTimeInDetention(int leftTimeInDetention) {
        this.leftTimeInDetention = leftTimeInDetention;
    }

    public void goDetention() {
        token.moveTo(9);
        this.leftTimeInDetention = 3;
    }

    public void outDetention() {
        this.leftTimeInDetention = 0;
    }

}
