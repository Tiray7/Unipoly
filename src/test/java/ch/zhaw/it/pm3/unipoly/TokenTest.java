package ch.zhaw.it.pm3.unipoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import ch.zhaw.it.pm3.unipoly.Config.TokenType;

public class TokenTest {
    Token t_token;

    /***
     * setting the test for token class
     */
    @Before
    public void setUp() {
        t_token = new Token(TokenType.LAB);
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
        assertEquals(TokenType.LAB, t_token.getType());
    }

    /***
     * test if get the right index
     */
    @Test
    public void testGetTileIndex() {
        assertEquals(0, t_token.getCurrFieldIndex());
    }

    /***
     * test if it move right from index field to another
     */
    @Test
    public void testMoveTo() {
        assertEquals(0, t_token.getCurrFieldIndex());
        t_token.moveTo(2);
        assertEquals(2, t_token.getCurrFieldIndex());
    }

    /***
     * test if its move right from one field index
     * to another by certain amount
     */
    @Test
    public void testMoveBy() {
        assertEquals(0, t_token.getCurrFieldIndex());
        t_token.moveBy(1);
        assertEquals(1, t_token.getCurrFieldIndex());
    }
}