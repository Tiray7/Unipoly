package ch.zhaw.it.pm3.unipoly;

public class Token {
    
    private TokenType type;
    private int prevFieldIndex = 0;
    private int currFieldIndex = 0;

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public int getPrevFieldIndex() {
        return prevFieldIndex;
    }

    public int getCurrFieldIndex() {
        return currFieldIndex;
    }

    public void moveTo(int fieldIndex) {
        prevFieldIndex = currFieldIndex;
        this.currFieldIndex = fieldIndex;
    }

    public void moveBy(int amount) {
        prevFieldIndex = currFieldIndex;
        currFieldIndex = prevFieldIndex + amount;
        if(currFieldIndex > 35){
            currFieldIndex -= 36;
        }
    }
}