package ch.zhaw.it.pm3.unipoly;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ch.zhaw.it.pm3.unipoly.Token;
import ch.zhaw.it.pm3.unipoly.TokenType;

public class TokenTest {
    Token t;
    @Before
    public void setUp() throws Exception {
        t = new Token(TokenType.LAB);
    }

    @Test
    public void testToken() {
        assertNotNull(t);
    }

    @Test
    public void testGetType() {
        assertEquals(t.getType(), TokenType.LAB);
    }

    @Test
    public void testGetTileIndex() {
        assertEquals(t.getCurrFieldIndex(), 0);
    }

    @Test
    public void testMoveTo() {
        assertEquals(t.getCurrFieldIndex(), 0);
        t.moveTo(2);
        assertEquals(t.getCurrFieldIndex(), 2);
    }

    @Test
    public void testMoveBy() {
        assertEquals(t.getCurrFieldIndex(), 0);
        t.moveBy(1);
        assertEquals(t.getCurrFieldIndex(), 1);
    }

}