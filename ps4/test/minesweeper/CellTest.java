package minesweeper;

import static org.junit.Assert.*;
import org.junit.Test;

public class CellTest {

    @Test
    public void testInitialState() {
        Cell c = new Cell(true);
        assertTrue(c.hasBomb());
        assertEquals(Cell.State.UNTOUCHED, c.getState());
    }

    @Test
    public void testFlagAndDeflag() {
        Cell c = new Cell(false);
        assertTrue(c.flag());
        assertEquals(Cell.State.FLAGGED, c.getState());
        assertTrue(c.deflag());
        assertEquals(Cell.State.UNTOUCHED, c.getState());
    }

    @Test
    public void testDig() {
        Cell c = new Cell(false);
        assertTrue(c.dig());
        assertEquals(Cell.State.DUG, c.getState());
    }

    @Test
    public void testRemoveBomb() {
        Cell c = new Cell(true);
        assertTrue(c.removeBomb());
        assertFalse(c.hasBomb());
    }
}