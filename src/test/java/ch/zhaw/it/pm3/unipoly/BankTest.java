package ch.zhaw.it.pm3.unipoly;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BankTest {
    Bank bank;
    Player player;

    /***
     * setting test up before start testing
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        bank = new Bank();
        player = new Player(-1, "Dilan", Token.TokenType.EINSTEIN);
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
        bank.awardFreeParking(player);
        assertEquals(1600, player.getMoney(), 0);
    }

}