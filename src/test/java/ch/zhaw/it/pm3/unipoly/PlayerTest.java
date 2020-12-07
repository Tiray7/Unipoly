package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;

import ch.zhaw.it.pm3.unipoly.Player;
import ch.zhaw.it.pm3.unipoly.Token.TokenType;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerTest {

    Player player;

    /***
     * setting player const. for test
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        player = new Player(0,"Jack", Token.TokenType.ATOM);
    }

    /***
     * test player const with 15000 as money and
     * name as jack
     * and token as atom
     */
    @Test
    public void testPlayer() {
        assert(player.getMoney() == 1500);
        assert(player.getName().equals("Jack"));
        assert(player.getToken().getType() == Token.TokenType.ATOM);
    }

    /***
     * test getting the right name
     */
    @Test
    public void testGetName() {
        assertEquals(player.getName(), "Jack");
    }

    /***
     * testing if get the right amount of money
     */
    @Test
    public void testGetMoney() {
        assertTrue(player.getMoney() == 1500);
    }

    /***
     * test if the transfer is done from each player
     */
    @Test
    public void testTransfer() {
        Player player2 = new Player(1,"Sam", Token.TokenType.CRADLE);
        assertEquals(player.getMoney(), 1500, 0);
        assertEquals(player.transferMoneyTo(player2,100),100,0);
        assertEquals(player.getMoney(), 1400, 0);
    }

}