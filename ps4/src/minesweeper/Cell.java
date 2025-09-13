package minesweeper;

public class Cell {
    /**
     *  AF:
     *      Cell represents a square in Minesweeper,
     *      which may contain a bomb and has a state (untouched, flagged, dug).
     *  RI:
     *      state != null (no need for checkRep())
     *  Safety from rep exposure:
     *      All fields are declared private.
     *      Both fields use immutable or primitive types
     *  Thread safety:
     *      monitor pattern.
     */

    private boolean hasBomb;
    private State state;

    public enum State {
        UNTOUCHED,
        FLAGGED,
        DUG
    }

    public Cell(boolean hasBomb) {
        this.hasBomb = hasBomb;
        this.state = State.UNTOUCHED;
    }

    public Cell(boolean hasBomb, State state) {
        this.hasBomb = hasBomb;
        this.state = state;
    }

    /** @return true iff this cell has a bomb */
    public synchronized boolean hasBomb() {
        return hasBomb;
    }

    /** @return current state */
    public synchronized State getState() {
        return state;
    }

    /**
     * Change the state to FLAGGED, if currently UNTOUCHED.
     * @return true if the state was changed, false otherwise
     */
    public synchronized boolean flag() {
        if (state == State.UNTOUCHED) {
            state = State.FLAGGED;
            return true;
        }
        return false;
    }

    /**
     * If hasBomb is true, change hasBomb to false.
     * @return true if the hasBomb was changed to false.
     */
    public synchronized boolean removeBomb() {
        if (this.hasBomb()) {
            hasBomb = false;
            return true;
        }
        return false;
    }

    /**
     * Change the state to UNTOUCHED, if currently FLAGGED.
     * @return true if the state was changed, false otherwise
     */
    public synchronized boolean deflag() {
        if (state == State.FLAGGED) {
            state = State.UNTOUCHED;
            return true;
        }
        return false;
    }

    /**
     * Dig this cell. If the cell is UNTOUCHED, change to DUG.
     * @return true if state changed to DUG; false if already FLAGGED or DUG.
     *         Client must handle BOOM logic themselves.
     */
    public synchronized boolean dig() {
        if (state == State.UNTOUCHED) {
            state = State.DUG;
            return true;
        }
        return false;
    }
}

