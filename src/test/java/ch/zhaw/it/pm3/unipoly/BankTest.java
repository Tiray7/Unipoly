package ch.zhaw.it.pm3.unipoly;
import org.junit.Before;
import org.junit.Test;

import ch.zhaw.it.pm3.unipoly.Bank;
import ch.zhaw.it.pm3.unipoly.Player;
import ch.zhaw.it.pm3.unipoly.TokenType;

import static org.junit.Assert.assertEquals;

public class BankTest {
    Bank bank;
    Player playa;

    /***
     * setting test up before start testing
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        bank = new Bank();
        playa = new Player(-1, "Dilan", TokenType.EINSTEIN);
    }

    /***
     * test bank cont. with 2000000
     */
    @Test
    public void testBank() {
        assert(bank.getMoney() == 2000000);
        assert(bank.getName().equals("Bank"));
    }

    /***
     * test free parking method
     */
    @Test
    public void testGetFreeParking() {
        assert(bank.getFreeParking() == 100);
    }

    /***
     * test free parking award
     */
    @Test
    public void testAwardFreeParking() {
        bank.awardFreeParking(playa);
        assertEquals(1600, playa.getMoney(), 0);
    }

}