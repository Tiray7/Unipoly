package com.example.Softwareproject3;

    public class Player extends Owner {

        private Token token;
        private boolean bankrupt = false;
        private boolean JailCard = false;
        int stayJailedOneRund = 0;
        int doublesRolled = 0;

        public Player(String name, TokenType tokenType) {
            super(name, 1500);
            token = new Token(tokenType);
        }

        public boolean getJailCard(){
            return JailCard;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token){
            this.token = token;
        }

        public void setJailCard(boolean outJail){
            JailCard = outJail;
        }

        public boolean getBankrupt() {
            return bankrupt;
        }

        public void setBankrupt(boolean isBankrupt) {
            this.bankrupt = isBankrupt;
        }

        public boolean isJailed() {
            return stayJailedOneRund > 0;
        }

        public void jail(){
            token.moveTo(9);
            doublesRolled = 0;
            this.stayJailedOneRund = 3;
        }
    }

