package expressivo;


import java.util.Map;
import java.util.Objects;

/**
 * Immutable addition of two expression
 */
public class Addition implements Expression {
    private final Expression expr1;
    private final Expression expr2;

    /*
     *  AF(expr1, expr2): addition of two expressions
     *  RI: No extra requirements
     *  Safety from rep exposure:
     *       left and right are private and final
     *       Expression is immutable
     */

    private void checkRep() {
        assert expr1 != null;
        assert expr2 != null;
    }

    public Addition(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        checkRep();
    }

    @Override
    public String toString() {
        return "(" + expr1.toString() + " + " + expr2.toString() + ")";
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Addition)) {
            return false;
        }
        Addition thatExpression = (Addition) thatObject;
        return this.expr1.equals(thatExpression.expr1) && this.expr2.equals(thatExpression.expr2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr1, expr2);
    }

    @Override
    public Expression differentiate(String variable) {
        return Expression.add(expr1.differentiate(variable),  expr2.differentiate(variable));
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
        Expression updatedExpr1 = expr1.simplify(environment);
        Expression updatedExpr2 = expr2.simplify(environment);
        if (updatedExpr1.isNumber() && updatedExpr2.isNumber()) {
            return Expression.make(Double.parseDouble(updatedExpr1.toString()) + Double.valueOf(updatedExpr2.toString()));
        }
        return Expression.add(updatedExpr1, updatedExpr2);
    }
}
