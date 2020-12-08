package ch.zhaw.it.pm3.unipoly;

import static ch.zhaw.it.pm3.unipoly.Config.BANK_START_MONEY;

public class Bank extends Owner {

    /***
     * initial  money is 2000000
     */
    public Bank() {
        super(-1, "Bank", BANK_START_MONEY);
    }


}
