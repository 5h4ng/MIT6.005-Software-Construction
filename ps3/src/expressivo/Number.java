package expressivo;

import java.util.Objects;

/**
 * Immutable numeric constant in an expression
 */
public class Number implements Expression {
    private final double value;

    /*
     * AF(value): non-negative numbers in decimal representation
     * RI: value >= 0
     * Safety from Rep Exposure: All fields are final and private
     */

    private void checkRep() {
        assert value >= 0;
    }

    public Number(double value) {
        this.value = value;
        checkRep();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Number)) {
            return false;
        }
        Number thatNumber = (Number) thatObject;
        return this.value == thatNumber.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}



