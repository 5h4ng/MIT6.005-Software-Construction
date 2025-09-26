/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

public class Board {

    /**
     *  AF:
     *      This class represents a game board of width x height cells.
     *      Each cell records whether it has bomb and its current state.
     *      The (x,y) coordinates start at (0,0) in the top-left corner,
     *        extend horizontally to the right in the X direction,
     *        and vertically downwards in the Y direction.
     *  RI:
     *      - width > 0, height > 0;
     *  rep exposure:
     *      - Grid is final and private.
     *      - Snapshot of board is returned as immutable String.
     *  thread safety:
     *      - Using monitor pattern, all accesses to board happen with in Board method,
     *      - which all guarded by Board's lock
     */

    private final int width;
    private final int height;
    private final Cell[][] grid;

    public Board(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                grid[x][y] = new Cell(false);
            }
        }
    }

    /**
     * Make a board with bombs located according to the given bombInfo.
     * @param width the width of the board
     * @param height the height of the board
     * @param bombInfo bomb layout for the board, where bombInfo[x][y] is true iff
     *                 the cell at coordinates (x,y) should contain a bomb.
     */
    public Board(int width, int height, boolean[][] bombInfo) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        if (bombInfo.length != width) throw new IllegalArgumentException();
        for (int x=0; x<width; x++) {
            if (bombInfo[x].length != height) throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                grid[x][y] = new Cell(bombInfo[x][y]);
            }
        }
    }

    /**
     * Get the width of the board.
     * @return width of the board.
     */
    public synchronized int getWidth() {
        return width;
    }

    /**
     * Get the height of the board.
     * @return height of the board.
     */
    public synchronized int getHeight() {
        return height;
    }

    /**
     * Get the state at (x,y).
     * @param x x direction coordinate, 0 <= x < width
     * @param y y direction coordinate, 0 <= y < height
     * @return (x,y)'s state
     */
    public synchronized Cell.State getCellState(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return grid[x][y].getState();
    }

    /**
     * Check if (x,y) has bomb.
     * @param x x direction coordinate, 0 <= x < width
     * @param y y direction coordinate, 0 <= y < height
     * @return true if (x,y) has bomb, false otherwise.
     */
    public synchronized boolean hasBomb(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return grid[x][y].hasBomb();
    }

    /**
     * Try to dig at (x,y). (x,y) must be a valid position and in UNTOUCHED state.
     * If (x,y) contains a bomb, change it so that it contains no bomb.
     * If (x,y) doesn't contain a bomb, mark dug, and if neighbors contain 0 bombs,
     *           recursively dig all untouched neighbors.
     * @param x x direction coordinate, 0 <= x < width
     * @param y y direction coordinate, 0 <= y < height
     * @return true if a bomb was dug, otherwise return false.
     */
    public synchronized boolean dig(int x, int y) {
        if (!isValidPosition(x, y) || grid[x][y].getState() != Cell.State.UNTOUCHED) {
            throw new IllegalArgumentException("Invalid position");
        }
        Cell cell = grid[x][y];
        cell.dig();

        boolean bombFlag = false;
        // case: bomb
        if (cell.hasBomb()) {
            cell.removeBomb();
            bombFlag = true;
        }

        // case: no bomb
        if (countNeighborBooms(x, y) == 0) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int xx = x + dx;
                    int yy = y + dy;
                    if (isValidPosition(xx, yy) && grid[xx][yy].getState() == Cell.State.UNTOUCHED) {
                        dig(xx, yy);
                    }
                }
            }
        }
        return bombFlag;
    }

    /**
     * Try to flag at (x,y). (x,y) must be a valid position and in UNTOUCHED state.
     * @param x x direction coordinate, 0 <= x < width
     * @param y y direction coordinate, 0 <= y < height
     */
    public synchronized void flag(int x, int y) {
        if (!isValidPosition(x, y) || grid[x][y].getState() != Cell.State.UNTOUCHED) {
            throw new IllegalArgumentException("Invalid position");
        }
        Cell cell = grid[x][y];
        cell.flag();
    }

    /**
     * Try to flag at (x,y). (x,y) must be a valid position and in FLAGGED state.
     * @param x x direction coordinate, 0 <= x < width
     * @param y y direction coordinate, 0 <= y < height
     */
    public synchronized void deflag(int x, int y) {
        if (!isValidPosition(x, y) || grid[x][y].getState() != Cell.State.FLAGGED) {
            throw new IllegalArgumentException("Invalid position");
        }
        Cell cell = grid[x][y];
        cell.deflag();
    }
    /**
     * Get the BOARD message:
     *      - “-” for squares with state untouched .
     *      - “F” for squares with state flagged .
     *      - “ ” (space) for squares with state dug and 0 neighbors that have a bomb.
     *      - integer COUNT in range [1-8] for squares with state dug and COUNT neighbors that have a bomb.
     * @return a String representing the BOARD message.
     */
    public synchronized String getBoardMessage() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid[x][y];
                String symbol;
                switch (cell.getState()) {
                    case UNTOUCHED:
                        symbol = "-";
                        break;
                    case FLAGGED:
                        symbol = "F";
                        break;
                    case DUG:
                        int bombNum = countNeighborBooms(x, y);
                        symbol = (bombNum == 0 ? " " : Integer.toString(bombNum));
                        break;
                    default:
                        throw new IllegalStateException("Invalid cell state");
                }
                sb.append(symbol);
                if (x < width - 1) {
                    sb.append(" ");
                }
            }
            if (y < height - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getBoardMessage();
    }

    // return true iff (x, y) is a valid position.
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Count the number of bombs around (x, y)
    private int countNeighborBooms(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException("Invalid position");
        }
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int xx = x + dx;
                int yy = y + dy;
                if (isValidPosition(xx,yy) && grid[xx][yy].hasBomb()) {
                    count++;
                }
            }
        }
        return count;
    }
}
