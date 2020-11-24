package ch.zhaw.it.pm3.unipoly;

public class ChanceCards {
    private String text;
    private ChanceCardType cardType;
    private int amount;

    public ChanceCards(String text, ChanceCardType cardType, int amount) {
        this.text = text;
        this.cardType = cardType;
        this.amount = amount;
    }

    public String getText() {
        return text;
    }

    public ChanceCardType getCardType() {
        return cardType;
    }

    public int getAmount() {
        return amount;
    }
}