package ch.zhaw.it.pm3.unipoly;

import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.Assert.assertEquals;

public class BoardTests {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testBoardFillUp() throws FieldIndexException {
        assertEquals(Config.FieldLabel.GO, board.getFieldTypeAtIndex(0));
        assertEquals(Config.FieldLabel.VISIT, board.getFieldTypeAtIndex(9));
        assertEquals(Config.FieldLabel.RECESS, board.getFieldTypeAtIndex(18));
        assertEquals(Config.FieldLabel.DETENTION, board.getFieldTypeAtIndex(27));
        assertEquals(Config.FieldLabel.CHANCE, board.getFieldTypeAtIndex(2));
        assertEquals(Config.FieldLabel.JUMP, board.getFieldTypeAtIndex(7));
        assertEquals(Config.FieldLabel.PROPERTY, board.getFieldTypeAtIndex(1));
    }

    @Test
    public void shouldReturnPropertyNameAtIndex() throws FieldIndexException {
        assertEquals("Geschichte",board.getPropertyNameAtIndex(1));
        assertEquals("Rechtswissenschaften",board.getPropertyNameAtIndex(35));
    }

    @Test
    public void shouldReturnRentFromProperty() {
        assertEquals(10, board.getRentFromProperty(1));
        assertEquals(200, board.getRentFromProperty(35));
    }

    @Test
    public void shouldReturnPropertyCost() {
        assertEquals(60, board.getCostFromProperty(1));
        assertEquals(400, board.getCostFromProperty(35));
    }

    @Test
    public void shouldRaiseRentOfProperty() {
        assertEquals(10, board.getRentFromProperty(1));
        board.raiseRentFromProperty(1);
        assertEquals(30, board.getRentFromProperty(1));
    }

    @Test
    public void shouldReturnPropertyOwner() {
        assertEquals(-1, board.getPropertyOwner(1));
    }

    @Test
    public void shouldThrowFieldIndexException() {
        assertThrows(FieldIndexException.class, () -> board.getPropertyNameAtIndex(67));
    }

    @Test
    public void fillModuleGroupeMaps(){
        board.getProperties().forEach((moduleGroupeIndex, fieldProperty) -> {
            fieldProperty.getModuleGroup().forEach(fieldPropertyOfSameModule -> {
                assertEquals(fieldProperty.getModuleGroupIndex(), fieldPropertyOfSameModule.getModuleGroupIndex());
            });
        });
    }
}
