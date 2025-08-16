package expressivo;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable multiplication of two expressions.
 */
public class Multiplication implements Expression {
    private final Expression expr1;
    private final Expression expr2;

    // Abstraction function:
    //   AF(expr1, expr2) = the product (expr1 * expr2)
    // Representation invariant:
    //   expr1 != null && expr2 != null
    // Safety from rep exposure:
    //   expr1 and expr2 are private and final, and Expression is immutable

    private void checkRep() {
        assert expr1 != null;
        assert expr2 != null;
    }
    
    public Multiplication(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        checkRep();
    }

    @Override
    public String toString() {
        return "(" + expr1.toString() + " * " + expr2.toString() + ")";
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Multiplication)) return false;
        Multiplication that = (Multiplication) thatObject;
        return this.expr1.equals(that.expr1) && this.expr2.equals(that.expr2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr1, expr2);
    }

    @Override
    public Expression differentiate(String variable) {
        return Expression.add(
                Expression.multiply(expr1, expr2.differentiate(variable)),
                Expression.multiply(expr2, expr1.differentiate(variable))
        );
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }




}