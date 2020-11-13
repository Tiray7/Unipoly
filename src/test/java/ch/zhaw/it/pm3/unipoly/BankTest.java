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

    @Before
    public void setUp() throws Exception {
        bank = new Bank();
        playa = new Player("Dilan", TokenType.EINSTEIN);
    }

    @Test
    public void testBank() {
        assert(bank.getMoney() == 2000000);
        assert(bank.getName().equals("Bank"));
    }

    @Test
    public void testGetFreeParking() {
        assert(bank.getFreeParking() == 100);
    }

    @Test
    public void testAwardFreeParking() {
        bank.awardFreeParking(playa);
        assertEquals(1600, playa.getMoney(), 0);
    }

}