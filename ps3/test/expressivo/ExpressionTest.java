/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {

    // Testing strategy
    //   TODO
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    
    // TODO tests for Expression
    @Test
    public void testMakeVariable() {
        Expression x = Expression.make("x");
        assertEquals("x", x.toString());
        assertEquals(x, Expression.make("x"));
        assertNotEquals(x, Expression.make("y"));
    }

    @Test
    public void testMakeNumber() {
        Expression n = Expression.make(42.0);
        assertEquals("42.0", n.toString());
        assertEquals(n, Expression.make(42));
        assertEquals(n.hashCode(), Expression.make(42).hashCode());
    }

    @Test
    public void testAddition() {
        Expression x = Expression.make("x");
        Expression n = Expression.make(2);
        Expression sum = Expression.add(x, n);
        assertEquals("(x + 2.0)", sum.toString());
        assertEquals(sum, Expression.add(Expression.make("x"), Expression.make(2)));
        assertNotEquals(sum, Expression.add(Expression.make("y"), Expression.make(2)));
    }

    @Test
    public void testMultiplication() {
        Expression x = Expression.make("x");
        Expression n = Expression.make(3);
        Expression prod = Expression.multiply(x, n);
        assertEquals("(x * 3.0)", prod.toString());
        assertEquals(prod, Expression.multiply(Expression.make("x"), Expression.make(3)));
        assertNotEquals(prod, Expression.multiply(Expression.make("y"), Expression.make(3)));
    }

    @Test
    public void testToStringNested() {
        Expression x = Expression.make("x");
        Expression y = Expression.make("y");
        Expression expr = Expression.add(Expression.multiply(x, y), Expression.make(1));
        assertEquals("((x * y) + 1.0)", expr.toString());
    }

    @Test
    public void testEqualsContract() {
        Expression x1 = Expression.make("x");
        Expression x2 = Expression.make("x");
        Expression x3 = Expression.make("x");
        // Reflexive
        assertEquals(x1, x1);
        // Symmetric
        assertEquals(x1, x2);
        assertEquals(x2, x1);
        // Transitive
        assertEquals(x2, x3);
        assertEquals(x1, x3);
    }

    @Test
    public void testNumberAndVariableNotEqual() {
        assertNotEquals(Expression.make("x"), Expression.make(1));
    }

    @Test
    public void testAdditionOrderMatters() {
        Expression a = Expression.add(Expression.make(1), Expression.make(2));
        Expression b = Expression.add(Expression.make(2), Expression.make(1));
        assertNotEquals(a, b);
    }

    @Test
    public void testMultiplicationOrderMatters() {
        Expression a = Expression.multiply(Expression.make("x"), Expression.make("y"));
        Expression b = Expression.multiply(Expression.make("y"), Expression.make("x"));
        assertNotEquals(a, b);
    }
}
