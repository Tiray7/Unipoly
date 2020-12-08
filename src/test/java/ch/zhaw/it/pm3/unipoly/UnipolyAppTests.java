package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import ch.zhaw.it.pm3.unipoly.Config.TokenType;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UnipolyAppTests {

    Player player;
    Player npc;
    Player joinedPlayer;

    @Autowired
    private Controller controller;

    @Autowired
    private UnipolyApp unipoly;

    @Before
    public void setUp() {
        player = new Player(0, "timRhomberg", TokenType.ATOM);
        npc = new Player(1, "franzFerdinand", TokenType.NPCI);
        joinedPlayer = new Player(2, "zahnfleischblutermurphy", TokenType.ONEPLUS);
    }

    @Order(1)
    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Order(2)
    @Test
    public void getIndexHtml() {
        String filename = controller.get().getFilename();
        assertEquals("index.html", filename);
    }

    @Order(3)
    @Test
    public void getState() throws IOException {
        HttpStatus status = controller.getState().getStatusCode();
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(4)
    @Test
    public void shouldJoinPlayer() throws IOException, FieldIndexException {
        //setup
        int playerJoined = 1;
        String name = "zahnfleischblutermurphy";
        List<Player> players;
        //work
        HttpStatus status = controller.join(name, TokenType.ONEPLUS).getStatusCode();
        players = controller.unipoly.getPlayers();
        joinedPlayer = players.get(playerJoined);
        //assert
        assertEquals("zahnfleischblutermurphy", joinedPlayer.getName());
        assertEquals(TokenType.ONEPLUS, joinedPlayer.getToken().getType());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(5)
    @Test
    public void shouldStartNewGame() throws IOException, FieldIndexException {
        //setup
        int amountOfNPCs = 1;
        //work
        HttpStatus status = controller.start(Config.Gamemode.SINGLE, amountOfNPCs).getStatusCode();
        Player player = controller.unipoly.getCurrentPlayer();
        //assert
        assertEquals("NPC1", player.getName());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(6)
    @Test
    public void shouldResetGame() throws IOException {
        //setup
        controller.unipoly.setCurrentPlayer(player);
        //work
        HttpStatus status = controller.resetGame().getStatusCode();
        //assert
        assertNull(controller.unipoly.getCurrentPlayer());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(7)
    @Test
    public void shouldRollADice() throws IOException, FieldIndexException {
        //setup
        int min = 1;
        int max = 6;
        int firstDice = 1;
        controller.unipoly.setCurrentPlayer(player);
        //work
        HttpStatus status = controller.rollDice(firstDice).getStatusCode();
        //assert
        assertEquals(firstDice, controller.unipoly.getFirstDice());
        assertThat(controller.unipoly.getSecondDice()).isBetween(min, max);
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(8)
    @Test
    public void shouldRollTwoDice() throws IOException {
        //setup
        int min = 1;
        int max = 6;
        unipoly.setCurrentPlayer(player);
        //work
        HttpStatus status = controller.rollTwoDice().getStatusCode();
        //assert
        assertThat(controller.unipoly.getFirstDice()).isBetween(min, max);
        assertThat(controller.unipoly.getSecondDice()).isBetween(min, max);
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(9)
    @Test
    public void shouldEndATurn() throws IOException, FieldIndexException {
        //setup
        controller.unipoly.getPlayers().add(player);
        controller.unipoly.getPlayers().add(npc);
        controller.unipoly.setCurrentPlayer(player);
        assertEquals("timRhomberg", controller.unipoly.getCurrentPlayer().getName());
        //work
        HttpStatus status = controller.endTurn().getStatusCode();
        //assert
        assertEquals("zahnfleischblutermurphy", controller.unipoly.getCurrentPlayer().getName());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(10)
    @Test
    public void shouldCheckFieldOptions() throws IOException, FieldIndexException {
        //setup
        Field field = new Field(Config.FieldLabel.VISIT, "This is a test.");
        controller.unipoly.setCurrentField(field);
        controller.unipoly.setCurrentPlayer(player);
        //work
        HttpStatus status = controller.checkFieldOptions().getStatusCode();
        //assert
        assertEquals("Zum Glück nur zu Besuch im Rektorat.", controller.unipoly.getdisplayMessage());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(11)
    @Test
    public void shouldJumpAPlayerToDesiredField() throws IOException, FieldIndexException {
        //setup
        int fieldIndex = 1;
        Field currentField = new Field(Config.FieldLabel.GO, "go");
        controller.unipoly.setCurrentPlayer(player);
        controller.unipoly.setCurrentField(currentField);
        assertEquals(Config.FieldLabel.GO, controller.unipoly.getcurrentField().getLabel());
        //work
        HttpStatus status = controller.jumpPlayer(fieldIndex).getStatusCode();
        //assert
        assertEquals(Config.FieldLabel.PROPERTY, controller.unipoly.getcurrentField().getLabel());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(12)
    @Test
    public void shouldBuyADesiredPropertyForTheCurrentPlayer() throws IOException {
        //setup
        int ownerIndex;
        int ownedFieldIndex = 1;
        Map<Integer, FieldProperty> ownedModulesForTesting;
        Field field = new Field(Config.FieldLabel.PROPERTY, "This is a test.");
        controller.unipoly.setCurrentField(field);
        controller.unipoly.setCurrentPlayer(player);
        controller.unipoly.getCurrentPlayer().getToken().setCurrentFieldIndex(ownedFieldIndex);
        //work
        HttpStatus status = controller.userWantsToBuy().getStatusCode();
        ownedModulesForTesting = controller.unipoly.getCurrentPlayer().getownedModuls();
        //assert
        ownerIndex = controller.unipoly.getCurrentPlayer().getIndex();
        assertEquals(ownerIndex, ownedModulesForTesting.get(1).getOwnerIndex());
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(13)
    @Test
    public void getPayDetentionRansom() throws IOException, FieldIndexException {
        //setup
        int currentMoneyBefore;
        int currentMoneyAfter;
        int expectedBefore = 1500;
        int expectedAfter = 1400;
        controller.unipoly.setCurrentPlayer(player);
        currentMoneyBefore = controller.unipoly.getCurrentPlayer().getMoney();
        assertEquals(expectedBefore, currentMoneyBefore);
        //work
        HttpStatus status = controller.payDetentionRansom().getStatusCode();
        currentMoneyAfter = controller.unipoly.getCurrentPlayer().getMoney();
        //assert
        assertEquals(expectedBefore, currentMoneyBefore);
        assertEquals(expectedAfter, currentMoneyAfter);
        assertEquals(HttpStatus.ACCEPTED, status);
    }

    @Order(14)
    @Test
    public void getLeaveDetention() throws IOException, FieldIndexException {
        //setup
        int timeInDetentionBefore = 3;
        int outOfDetention = 0;
        controller.unipoly.setCurrentPlayer(player);
        controller.unipoly.getCurrentPlayer().setLeftTimeInDetention(timeInDetentionBefore);
        //work
        HttpStatus status = controller.leaveDetention().getStatusCode();
        //assert
        assertEquals(outOfDetention, controller.unipoly.getCurrentPlayer().getleftTimeInDetention());
        assertEquals(HttpStatus.ACCEPTED, status);
    }
}
