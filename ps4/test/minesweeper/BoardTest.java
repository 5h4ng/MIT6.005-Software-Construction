package minesweeper;

import static org.junit.Assert.*;
import org.junit.Test;

public class BoardTest {

    // ----------- Constructor tests -----------

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBoardSize() {
        new Board(0, 5);
    }

    @Test
    public void testEmptyBoardInit() {
        Board board = new Board(3, 3);
        assertEquals(3, board.getWidth());
        assertEquals(3, board.getHeight());
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                assertEquals(Cell.State.UNTOUCHED, board.getCellState(x,y));
            }
        }
    }

    @Test
    public void testBombInit() {
        boolean[][] bombs = {
                {true,false},
                {false,true}
        };
        Board board = new Board(2,2,bombs);
        assertTrue(board.dig(0,0)); // contains bomb
        assertFalse(board.dig(1,0)); // no bomb
    }

    // ----------- getCellState -----------

    @Test(expected = IllegalArgumentException.class)
    public void testGetCellStateInvalid() {
        Board board = new Board(2,2);
        board.getCellState(5,5); // invalid pos
    }

    // ----------- hasBomb -----------

    @Test
    public void testHasBombTrueAndFalse() {
        boolean[][] bombs = {
                {true, false},
                {false, true}
        };
        Board board = new Board(2,2,bombs);
        assertTrue(board.hasBomb(0,0));  // 有雷
        assertFalse(board.hasBomb(0,1)); // 无雷
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasBombInvalid() {
        Board board = new Board(2,2);
        board.hasBomb(5,5); // 越界坐标
    }

    // ----------- Dig -----------

    @Test
    public void testDigBombRemovesBomb() {
        boolean[][] bombs = { {true} };
        Board board = new Board(1,1,bombs);
        assertTrue(board.dig(0,0)); // bomb removed
        assertEquals(false, board.hasBomb(0,0));
    }

    @Test
    public void testDigEmptyRecursion() {
        Board board = new Board(2,2);
        boolean result = board.dig(0,0);
        assertFalse(result);
        // All should be dug because no bombs
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                assertEquals(Cell.State.DUG, board.getCellState(x,y));
            }
        }
    }

    // ----------- Flag / Deflag -----------

    @Test
    public void testFlagDeflag() {
        Board board = new Board(2,2);
        board.flag(0,0);
        assertEquals(Cell.State.FLAGGED, board.getCellState(0,0));
        board.deflag(0,0);
        assertEquals(Cell.State.UNTOUCHED, board.getCellState(0,0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFlag() {
        Board board = new Board(2,2);
        board.flag(5,5);
    }

    // ----------- Board message -----------

    @Test
    public void testBoardMessageFormat() {
        Board board = new Board(2,1);
        String msg = board.getBoardMessage().trim();
        // Should be "- -" (two untouched cells with space between)
        assertEquals("- -", msg);
    }

    @Test
    public void testLargeBoardAllClear() {
        Board board = new Board(5,5);
        board.dig(2,2); // no bombs anywhere
        for (int x=0; x<5; x++) {
            for (int y=0; y<5; y++) {
                assertEquals(Cell.State.DUG, board.getCellState(x,y));
            }
        }
    }

    // ----------- Advanced and Integrated Test -----------
    @Test
    public void testBoundaryDig() {
        Board board = new Board(3,3);
        board.dig(0,0);
        for (int x=0; x<3; x++) {
            for (int y=0; y<3; y++) {
                assertEquals(Cell.State.DUG, board.getCellState(x,y));
            }
        }
    }

    @Test
    public void testBombCountAccurate() {
        boolean[][] bombs = {
                {false, true, false},
                {false, false, false},
                {false, false, false}
        };
        Board board = new Board(3,3,bombs);
        board.dig(1,1); // center
        String msg = board.getBoardMessage();
        assertTrue(msg.contains("1"));
    }

    @Test
    public void testFlagDeflagDigFlow() {
        Board board = new Board(2,2);
        board.flag(0,0);
        assertEquals(Cell.State.FLAGGED, board.getCellState(0,0));
        board.deflag(0,0);
        board.dig(0,0);
        assertEquals(Cell.State.DUG, board.getCellState(0,0));
        try {
            board.flag(0,0);
            fail("Expected exception when flagging a dug cell");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testBoardMessageExactOutput() {
        boolean[][] bombs = new boolean[3][3];
        bombs[1][0] = true;
        Board board = new Board(3,3,bombs);
        board.dig(0,0);
        String expectedRow0 = "1 - -";
        String[] lines = board.getBoardMessage().trim().split("\n");
        assertEquals(expectedRow0, lines[0]);
    }

    // ----------- 7 x 7 Test -----------
    private Board createBoard7x7() {
        boolean[][] bombs = new boolean[7][7];
        bombs[0][6] = true;
        return new Board(7,7,bombs);
    }

    @Test
    public void testLookInitial7x7() {
        Board board = createBoard7x7();
        String[] lines = board.getBoardMessage().trim().split("\n");
        assertEquals(7, lines.length);
        for (String line : lines) {
            assertEquals("- - - - - - -", line);
        }
    }

    @Test
    public void testBombNeighborCounts7x7() {
        Board board = createBoard7x7();
        board.dig(4, 1);
        String boardMessage = board.getBoardMessage();
        String[] lines = boardMessage.split("\n");

        char[][] expected = {
                {' ', ' ', ' ', ' ', ' ', ' ', ' '}, // row 0
                {' ', ' ', ' ', ' ', ' ', ' ', ' '}, // row 1
                {' ', ' ', ' ', ' ', ' ', ' ', ' '}, // row 2
                {' ', ' ', ' ', ' ', ' ', ' ', ' '}, // row 3
                {' ', ' ', ' ', ' ', ' ', ' ', ' '}, // row 4
                {'1', '1', ' ', ' ', ' ', ' ', ' '}, // row 5: (0,5)=1, (1,5)=1
                {'-', '1', ' ', ' ', ' ', ' ', ' '}  // row 6: (0,6)=-, (1,6)=1
        };

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                char actualChar = lines[y].charAt(x * 2);
                assertEquals("Cell (" + x + "," + y + ") should match expected",
                        expected[y][x], actualChar);

            }
        }
    }
}