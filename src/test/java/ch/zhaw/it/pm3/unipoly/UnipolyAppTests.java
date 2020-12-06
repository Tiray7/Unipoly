package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UnipolyAppTests {

    Player player;
    Player npc;

    @Autowired
    private Controller controller;

    @Autowired
    private UnipolyApp unipoly;

    @Before
    public void setUp() {
        player = new Player(1, "timRhomberg", Token.TokenType.ATOM);
        npc = new Player(2, "franzFerdinand", Token.TokenType.NPCI);
    }

    /**
     * Systemtests
     */
    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void getIndexHtml() {
        String filename = controller.get().getFilename();
        assertEquals("index.html", filename);
    }

    @Test
    public void getState() throws IOException {
        HttpStatus status = controller.getState().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getJoin() throws IOException, FieldIndexException {
        String name = "jim";
        HttpStatus status = controller.join(name, Token.TokenType.CRADLE).getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getStart() throws IOException, FieldIndexException {
        int amountOfNPCs = 1;
        HttpStatus status = controller.start(UnipolyApp.Gamemode.SINGLE, amountOfNPCs).getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getResetGame() throws IOException {
        HttpStatus status = controller.resetGame().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getRollDice() throws IOException, FieldIndexException {
        int firstDice = 1;
        unipoly.setCurrentPlayer(player);
        HttpStatus status = controller.rollDice(firstDice).getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getRollTwoDice() throws IOException {
        unipoly.setCurrentPlayer(player);
        HttpStatus status = controller.rollTwoDice().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getEndTurn() throws IOException, FieldIndexException {
        unipoly.getPlayers().add(player);
        unipoly.getPlayers().add(npc);
        HttpStatus status = controller.endTurn().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getCheckFieldOptions() throws IOException, FieldIndexException {
        Field field = new Field(Config.FieldLabel.VISIT, "This is a test.");
        unipoly.setCurrentField(field);
        HttpStatus status = controller.checkFieldOptions().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getJumpPlayer() throws IOException, FieldIndexException {
        int fieldIndex = 1;
        HttpStatus status = controller.jumpPlayer(fieldIndex).getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);}

    @Test
    public void getUserWantsToBuy() throws IOException {
        Field field = new Field(Config.FieldLabel.PROPERTY, "This is a test.");
        unipoly.setCurrentField(field);
        unipoly.getCurrentPlayer().getToken().setCurrentFieldIndex(1);
        HttpStatus status = controller.userWantsToBuy().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getPayDetentionRansom() throws IOException {
        HttpStatus status = controller.payDetentionRansom().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    @Test
    public void getLeaveDetention() throws IOException {
        HttpStatus status = controller.leaveDetention().getStatusCode();
        String statusCode = status.toString();
        assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
    }

    /**
     * Unittest
     */
    @Test
    public void testRollDice() {
        unipoly.setCurrentPlayer(player);
        Integer[] values = {1, 2, 3, 4, 5, 6};
        List<Integer> range = Arrays.asList(values);

        unipoly.rollTwoDice();
        int firstDice = unipoly.getFirstDice();
        int secondDice = unipoly.getSecondDice();

        assertTrue(range.contains(firstDice));
        assertTrue(range.contains(secondDice));
    }

	/*@Test
	public void getPayOffDebt() throws IOException, FieldIndexException {
		Field field = new Field(Config.FieldLabel.PROPERTY, "This is a test.");
		unipoly.setCurrentField(field);
		unipoly.getCurrentPlayer().getToken().setCurrentFieldIndex(1);

		int[] fieldIndices = {1, 3};
		unipoly.getCurrentPlayer().setDebtor(unipoly.getBank());

		HttpStatus status = controller.payOffDebt(fieldIndices).getStatusCode();
		String statusCode = status.toString();
		assertEquals(HttpStatus.ACCEPTED.toString(), statusCode);
	}*/
}
