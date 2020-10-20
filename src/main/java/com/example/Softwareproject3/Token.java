package com.example.Softwareproject3;

public class Token {
    
    private TokenType type;

    private int prevFieldIndex = 0;

    private int currFieldIndex = 0;;

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public int getprevFieldIndex() {
        return prevFieldIndex;
    }

    public int getcurrFieldIndex() {
        return currFieldIndex;
    }

    public void moveTo(int fieldIndex) {
        prevFieldIndex = currFieldIndex;
        this.currFieldIndex = fieldIndex;
    }

    public void moveBy(int amount) {
        prevFieldIndex = currFieldIndex;
        currFieldIndex = prevFieldIndex + amount;
    }
}
