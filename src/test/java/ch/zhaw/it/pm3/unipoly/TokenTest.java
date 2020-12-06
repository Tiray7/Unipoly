package ch.zhaw.it.pm3.unipoly;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TokenTest {
    Token t_token;

    /***
     * setting the test for token class
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        t_token = new Token(Token.TokenType.LAB);
    }

    /***
     * test token const.
     */
    @Test
    public void testToken() {
        assertNotNull(t_token);
    }

    /***
     * testing if getting the right token type
     */
    @Test
    public void testGetType() {
        assertEquals(t_token.getType(), Token.TokenType.LAB);
    }

    /***
     * test if get the right index
     */
    @Test
    public void testGetTileIndex() {
        assertEquals(t_token.getCurrFieldIndex(), 0);
    }

    /***
     * test if it move right from index field to another
     */
    @Test
    public void testMoveTo() {
        assertEquals(t_token.getCurrFieldIndex(), 0);
        t_token.moveTo(2);
        assertEquals(t_token.getCurrFieldIndex(), 2);
    }

    /***
     * test if its move right from one field index
     * to another by certain amount
     */
    @Test
    public void testMoveBy() {
        assertEquals(t_token.getCurrFieldIndex(), 0);
        t_token.moveBy(1);
        assertEquals(t_token.getCurrFieldIndex(), 1);
    }

}