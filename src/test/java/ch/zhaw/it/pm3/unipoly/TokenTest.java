package ch.zhaw.it.pm3.unipoly;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TokenTest {
    Token t_token;

    @Before
    public void setUp() throws Exception {
        t_token = new Token(TokenType.LAB);
    }

    @Test
    public void testToken() {
        assertNotNull(t_token);
    }

    @Test
    public void testGetType() {
        assertEquals(t_token.getType(), TokenType.LAB);
    }

    @Test
    public void testGetTileIndex() {
        assertEquals(t_token.getCurrFieldIndex(), 0);
    }

    @Test
    public void testMoveTo() {
        assertEquals(t_token.getCurrFieldIndex(), 0);
        t_token.moveTo(2);
        assertEquals(t_token.getCurrFieldIndex(), 2);
    }

    @Test
    public void testMoveBy() {
        assertEquals(t_token.getCurrFieldIndex(), 0);
        t_token.moveBy(1);
        assertEquals(t_token.getCurrFieldIndex(), 1);
    }

}