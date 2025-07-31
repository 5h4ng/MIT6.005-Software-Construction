package expressivo;

import java.util.Objects;

/**
 * Immutable variable in an expression
 */
public class Variable implements Expression {
    private final String var;

    /*
     * Abstraction function (AF):
     *   AF(var) = the variable named var (e.g., "x", "foo")
     *
     * Representation invariant (RI):
     *   - var is not null
     *   - var is not empty
     *   - var contains only letters a-z, A-Z (no spaces, no digits, no underscores)
     *
     * Safety from rep exposure:
     *   - var is private and final
     *   - String is immutable
     */

    /**
     * Check that the rep invariant holds.
     */
    private void checkRep() {
        assert var != null : "variable name is null";
        assert !var.isEmpty() : "variable name is empty";
        assert var.matches("[a-zA-Z]+") : "variable name must only contain letters";
    }

    public Variable(String var) {
        this.var = var;
        checkRep();
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Variable)) return false;
        Variable thatVariable = (Variable) thatObject;
        return this.var.equals(thatVariable.var);
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }


}
