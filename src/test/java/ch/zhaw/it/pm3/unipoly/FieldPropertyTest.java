package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class FieldPropertyTest {
        Board board;
        List<Player> players;
        Bank bank;


        /***
         * setting test up before start testing
         */
        @Before
        public void setUp() {
            board = new Board();
            bank = new Bank();
            bank.setownedModuls(new HashMap<Integer, FieldProperty>(board.getProperties()));
            players = new LinkedList<Player>();
                players.add(new Player(0,"Jack", Token.TokenType.ATOM));
                players.add(new Player(1,"John", Token.TokenType.EINSTEIN));

        }

        @Test
        public void OnePlayerOwnsWholeModuleGroupTest() {
            players.get(0).buyPropertyFrom(bank, 1);
            players.get(0).buyPropertyFrom(bank, 3);

            board.getProperties().get(1).checkAndRaiseRent();

            assertEquals(board.getProperties().get(1).getRentLV3(), board.getProperties().get(1).getCurrentRent());
            assertEquals(board.getProperties().get(3).getRentLV3(), board.getProperties().get(3).getCurrentRent());

            players.get(0).buyPropertyFrom(bank, 5);
            players.get(0).buyPropertyFrom(bank, 6);
            players.get(0).buyPropertyFrom(bank, 8);
            board.getProperties().get(8).checkAndRaiseRent();
            assertEquals(board.getProperties().get(5).getRentLV3(), board.getProperties().get(5).getCurrentRent());
            assertEquals(board.getProperties().get(6).getRentLV3(), board.getProperties().get(6).getCurrentRent());
            assertEquals(board.getProperties().get(8).getRentLV3(), board.getProperties().get(8).getCurrentRent());

        }
        @Test
        public void WholeModuleGroupIsOwnedTest() {
            players.get(0).buyPropertyFrom(bank, 1);
            players.get(1).buyPropertyFrom(bank, 3);

            board.getProperties().get(1).checkAndRaiseRent();
            assertEquals(board.getProperties().get(1).getRentLV2(), board.getProperties().get(1).getCurrentRent());
            assertEquals(board.getProperties().get(3).getRentLV2(), board.getProperties().get(3).getCurrentRent());
        }
        @Test
        public void ModuleGroupPartiallyOwnedTest() {
            players.get(0).buyPropertyFrom(bank, 1);

            board.getProperties().get(1).checkAndRaiseRent();
            assertEquals(board.getProperties().get(1).getRentLV2(), board.getProperties().get(1).getCurrentRent());
            assertEquals(board.getProperties().get(3).getRentLV1(), board.getProperties().get(3).getCurrentRent());
        }
}
