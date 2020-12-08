package ch.zhaw.it.pm3.unipoly;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.zhaw.it.pm3.unipoly.Config.TokenType;

import static junit.framework.TestCase.assertEquals;

public class PlayerTest {

    Player player;

    /***
     * setting player const. for test
     */
    @Before
    public void setUp() {
        player = new Player(0, "Jack", TokenType.ATOM);
    }

    /***
     * test player const with 15000 as money and
     * name as jack
     * and token as atom
     */
    @Test
    public void testPlayer() {
        assert (player.getMoney() == 1500);
        assert (player.getName().equals("Jack"));
        assert (player.getToken().getType() == TokenType.ATOM);
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
        Assert.assertEquals(1500, player.getMoney());
    }

    /***
     * test if the transfer is done from each player
     */
    @Test
    public void testTransfer() {
        Player player2 = new Player(1, "Sam", TokenType.CRADLE);
        assertEquals(player.getMoney(), 1500, 0);
        player.transferMoneyTo(player2, 100);
        assertEquals(player.getMoney(), 1400, 0);
        assertEquals(player2.getMoney(), 1600, 0);
    }

}