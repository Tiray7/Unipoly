package com.example.Softwareproject3;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class UnipolyAppTests {
    UnipolyApp unipolyApp;

    @Before
    public void setUp() {
        unipolyApp = new UnipolyApp();
    }

    @Test
    public void testRollDice() {
        Integer[] values = {1, 2, 3, 4, 5, 6};
        List<Integer> range = Arrays.asList(values);
        unipolyApp.rollDice();
        int firstDice = unipolyApp.getFirstDice();
        int secondDice = unipolyApp.getSecondDice();

        assertTrue(range.contains(firstDice));
        assertTrue(range.contains(secondDice));
    }
}
