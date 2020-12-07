package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class FieldPropertyTest {
    private Board board;
    private List<Player> players;
    private Bank bank;
    private FieldProperty propertyOne;
    private FieldProperty propertyTwo;


    @Before
    public void setUp() {
        board = new Board();
        bank = new Bank();
        bank.setownedModuls(new HashMap<>(board.getProperties()));
        players = new LinkedList<>();
        players.add(new Player(0,"Jack", Token.TokenType.ATOM));
        players.add(new Player(1,"John", Token.TokenType.EINSTEIN));
        propertyOne = new FieldProperty("Geschichte", Config.FieldLabel.PROPERTY, 60, 10, 30, 90, 160, 250, 0);
        propertyTwo =  new FieldProperty("Geographie", Config.FieldLabel.PROPERTY, 60, 20, 60, 180, 320, 450, 0);
    }

    @Test
    public void shouldRaiseRentAndETCS() {
        int expectedRentLevel1 = 10;
        int expectedRentLevel2 = 30;
        int expectedRentLevel3 = 90;
        int expectedRentLevel4 = 160;
        int expectedRentLevel5 = 250;

        assertEquals(expectedRentLevel1, propertyOne.getCurrentRent());
        propertyOne.raiseRentAndECTS();
        assertEquals(expectedRentLevel2, propertyOne.getCurrentRent());
        propertyOne.raiseRentAndECTS();
        assertEquals(expectedRentLevel3, propertyOne.getCurrentRent());
        propertyOne.raiseRentAndECTS();
        assertEquals(expectedRentLevel4, propertyOne.getCurrentRent());
        propertyOne.raiseRentAndECTS();
        assertEquals(expectedRentLevel5, propertyOne.getCurrentRent());
    }

    @Test
    public void shouldDecreaseRentAndECTS() {
        int expectedRentLevel1 = 20;
        int expectedRentLevel2 = 60;
        int expectedRentLevel3 = 180;
        int expectedRentLevel4 = 320;
        int expectedRentLevel5 = 450;

        propertyTwo.setCurrentRent(expectedRentLevel5);

        assertEquals(expectedRentLevel5, propertyTwo.getCurrentRent());
        propertyTwo.decreaseRentAndECTS();
        assertEquals(expectedRentLevel4, propertyTwo.getCurrentRent());
        propertyTwo.decreaseRentAndECTS();
        assertEquals(expectedRentLevel3, propertyTwo.getCurrentRent());
        propertyTwo.decreaseRentAndECTS();
        assertEquals(expectedRentLevel2, propertyTwo.getCurrentRent());
        propertyTwo.decreaseRentAndECTS();
        assertEquals(expectedRentLevel1, propertyTwo.getCurrentRent());
    }

    @Test
    public void moduleGroupPartiallyOwnedTest() throws FieldIndexException {
        players.get(0).buyPropertyFrom(bank, 1);

        board.checkAndRaiseRentAndECTS(board.getFieldPropertyAtIndex(1));
        assertEquals(board.getProperties().get(1).getRentLV2(), board.getProperties().get(1).getCurrentRent());
        assertEquals(board.getProperties().get(3).getRentLV1(), board.getProperties().get(3).getCurrentRent());
    }

    @Test
    public void resetLevelTest() throws FieldIndexException {
    players.get(0).buyPropertyFrom(bank, 1);
    players.get(1).buyPropertyFrom(bank, 3);

    board.checkAndRaiseRentAndECTS(board.getFieldPropertyAtIndex(1));
    board.checkAndRaiseRentAndECTS(board.getFieldPropertyAtIndex(1));
    board.resetLevelAll(0);
    assertEquals(board.getProperties().get(1).getRentLV1(),board.getProperties().get(1).getCurrentRent());
    assertEquals(10, board.getProperties().get(1).getCurrentECTSLevel());
    }
}
