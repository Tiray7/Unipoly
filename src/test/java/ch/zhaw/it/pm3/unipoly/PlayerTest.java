package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;

import ch.zhaw.it.pm3.unipoly.Player;
import ch.zhaw.it.pm3.unipoly.TokenType;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerTest {

    Player player;

    @Before
    public void setUp() throws Exception {
        player = new Player(0,"Jack",TokenType.ATOM);
    }

    @Test
    public void testPlayer() {
        assert(player.getMoney() == 1500);
        assert(player.getName().equals("Jack"));
        assert(player.getToken().getType() == TokenType.ATOM);
    }

    @Test
    public void testGetName() {
        assertEquals(player.getName(), "Jack");
    }

    @Test
    public void testGetMoney() {
        assertTrue(player.getMoney() == 1500);
    }

    @Test
    public void testTransfer() {
        Player player2 = new Player(1,"Sam", TokenType.CRADLE);
        assertEquals(player.getMoney(), 1500, 0);
        // TODO: assertEquals(player.transferMoneyTo(player2, 100), 100, 0);
        assertEquals(player.getMoney(), 1400, 0);
    }

}