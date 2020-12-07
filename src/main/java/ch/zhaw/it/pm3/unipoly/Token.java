package ch.zhaw.it.pm3.unipoly;

public class Token {

    private TokenType type;
    private int prevFieldIndex = 0;
    private int currFieldIndex = 0;

    enum TokenType {
        ATOM, ONEPLUS, EINSTEIN, CRADLE, EQUATION, HELIUM, LAB, LIGHTBULB, BANK, NPCI, NPCII, NPCIII
    }

    /***
     * token constructor
     * 
     * @param type present token type
     */
    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() { return type; }
    public int getPrevFieldIndex() { return prevFieldIndex; }
    public int getCurrFieldIndex() { return currFieldIndex; }

    public void setCurrentFieldIndex(int fieldIndex) {
        this.currFieldIndex = fieldIndex;
    }

    /***
     * moveTo is a method to moving from field to another
     * 
     * @param fieldIndex present which field
     */
    public void moveTo(int fieldIndex) {
        prevFieldIndex = 0;
        this.currFieldIndex = fieldIndex;
    }

    /***
     * moveBy is a method to know how log is it should be move by
     * 
     * @param amount present the amount of moving from the old field to the new
     *               field
     */
    public void moveBy(int amount) {
        prevFieldIndex = currFieldIndex;
        currFieldIndex = prevFieldIndex + amount;
        if (currFieldIndex > 35) {
            currFieldIndex -= 36;
        }
    }
}
