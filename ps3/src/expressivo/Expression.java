/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition
    //   Expression =
    //      Number(value: double) +
    //      Variable(name: String) +
    //      Addition(left: Expression, right: Expression) +
    //      Multiplication(left: Expression, rightL: Expression)
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        throw new RuntimeException("unimplemented");
    }
    
    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS3 handout.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();

    // factory methods
    /**
     * Create a variable expression.
     *
     * @param var variable name; must be a nonempty string of one or more case-sensitive letters (a-z, A-Z)
     * @return a Variable expression representing the variable named var
     */
    static Expression make(String var) {
        return new Variable(var);
    }

    /**
     * Create a numeric constant expression.
     *
     * @param val a non-negative number (integer or floating-point)
     * @return a Number expression representing the constant value val
     */
    static Expression make(double val) {
        return new Number(val);
    }

    /**
     * Create an addition expression: left + right.
     * The order of left and right matters.
     *
     * @param left  left operand (must not be null)
     * @param right right operand (must not be null)
     * @return an Addition expression representing (left + right)
     */
    static Expression add(Expression left, Expression right) {
        return new Addition(left, right);
    }

    /**
     * Create a multiplication expression: left * right.
     * The order of left and right matters.
     *
     * @param left  left operand (must not be null)
     * @param right right operand (must not be null)
     * @return a Multiplication expression representing (left * right)
     */
    static Expression multiply(Expression left, Expression right) {
        return new Multiplication(left, right);
    }



    // TODO more instance methods
    
}
