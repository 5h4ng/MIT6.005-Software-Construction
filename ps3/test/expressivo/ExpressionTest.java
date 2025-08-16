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
    
    
    // Test ADT, toString(), equals(), hashCode()
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

// ------------- Expression.parse() tests -------------

    @Test
    public void testParseNumber() {
        assertEquals(
                Expression.make(3),
                Expression.parse("3")
        );
        assertEquals(
                Expression.make(2.4),
                Expression.parse("2.4")
        );
    }

    @Test
    public void testParseVariable() {
        assertEquals(
                Expression.make("x"),
                Expression.parse("x")
        );
        assertEquals(
                Expression.make("foo"),
                Expression.parse("foo")
        );
    }

    @Test
    public void testParseAddition() {
        Expression expected = Expression.add(Expression.make(3), Expression.make(2.4));
        assertEquals(expected, Expression.parse("3 + 2.4"));
    }

    @Test
    public void testParseMultiplication() {
        Expression expected = Expression.multiply(Expression.make(3), Expression.make("x"));
        assertEquals(expected, Expression.parse("3 * x"));
    }

    @Test
    public void testParseAddMulPrecedence() {
        // 3 * x + 2.4  == (3 * x) + 2.4
        Expression expected = Expression.add(
                Expression.multiply(Expression.make(3), Expression.make("x")),
                Expression.make(2.4));
        assertEquals(expected, Expression.parse("3 * x + 2.4"));
    }

    @Test
    public void testParseParentheses() {
        // 3 * (x + 2.4)
        Expression expected = Expression.multiply(
                Expression.make(3),
                Expression.add(Expression.make("x"), Expression.make(2.4))
        );
        assertEquals(expected, Expression.parse("3 * (x + 2.4)"));
    }

    @Test
    public void testParseNestedParentheses() {
        // ((3 + 4) * x * x)
        Expression expected = Expression.multiply(
                Expression.multiply(
                        Expression.add(Expression.make(3), Expression.make(4)),
                        Expression.make("x")
                ),
                Expression.make("x")
        );
        assertEquals(expected, Expression.parse("((3 + 4) * x * x)"));
    }

    @Test
    public void testParseMultipleVariablesAddition() {
        // foo + bar + baz
        Expression expected = Expression.add(
                Expression.add(
                        Expression.make("foo"),
                        Expression.make("bar")
                ),
                Expression.make("baz")
        );
        assertEquals(expected, Expression.parse("foo + bar+baz"));
    }

    @Test
    public void testParseWhitespace() {
        // (2*x    )+    (    y*x    )
        Expression expected = Expression.add(
                Expression.multiply(Expression.make(2), Expression.make("x")),
                Expression.multiply(Expression.make("y"), Expression.make("x"))
        );
        assertEquals(expected, Expression.parse("(2*x    )+    (    y*x    )"));
    }

    /**
     * Test that parsing an expression and then converting it back to a string,
     * and parsing again, yields an equivalent Expression object.
     */
    @Test
    public void testParseLongExpression() {
        // 4 + 3 * x + 2 * x * x + 1 * x * x * (((x)))
        Expression expected = Expression.add(
                Expression.add(
                        Expression.add(
                                Expression.make(4),
                                Expression.multiply(Expression.make(3), Expression.make("x"))
                        ),
                        Expression.multiply(
                                Expression.multiply(Expression.make(2), Expression.make("x")),
                                Expression.make("x")
                        )
                ),
                Expression.multiply(
                        Expression.multiply(
                                Expression.multiply(Expression.make(1), Expression.make("x")),
                                Expression.make("x")
                        ),
                        Expression.make("x")
                )
        );
        assertEquals(expected, Expression.parse("4 + 3 * x + 2 * x * x + 1 * x * x * (((x)))"));
    }

    @Test
    public void testParseToStringRoundTrip() {
        String[] inputs = {
                "3", "2.4", "x", "x + 1", "1 + x", "x * x * x", "3 * (x + 2.4)"
        };
        for (String s : inputs) {
            Expression parsed = Expression.parse(s);
            assertEquals(parsed, Expression.parse(parsed.toString()));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseInvalidExpression1() {
        Expression.parse("3 *");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseInvalidExpression2() {
        Expression.parse("3 x");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseInvalidExpression3() {
        Expression.parse("(3 + 4");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseInvalidExpression4() {
        Expression.parse("3 * * 2");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseInvalidExpression5() {
        Expression.parse("");
    }

    // ------------- Expression.differentiate() tests -------------

    @Test
    public void testDifferentiateConstant() {
        Expression c = Expression.make(5);
        assertEquals(Expression.make(0), c.differentiate("x"));
    }

    @Test
    public void testDifferentiateVariableWithRespectToItself() {
        Expression x = Expression.make("x");
        assertEquals(Expression.make(1), x.differentiate("x"));
    }

    @Test
    public void testDifferentiateVariableOther() {
        Expression y = Expression.make("y");
        assertEquals(Expression.make(0), y.differentiate("x"));
    }

    @Test
    public void testDifferentiateAddition() {
        Expression expr = Expression.add(Expression.make("x"), Expression.make(3));
        // d/dx (x + 3) = 1 + 0
        Expression expected = Expression.add(Expression.make(1), Expression.make(0));
        assertEquals(expected, expr.differentiate("x"));
    }

    @Test
    public void testDifferentiateMultiplication() {
        Expression expr = Expression.multiply(Expression.make("x"), Expression.make("x"));
        // d/dx (x * x) = (1 * x) + (x * 1)
        Expression expected = Expression.add(
                Expression.multiply(Expression.make("x"), Expression.make(1)),
                Expression.multiply(Expression.make("x"), Expression.make(1))
        );
        assertEquals(expected, expr.differentiate("x"));
    }

    @Test
    public void testDifferentiateComplexExpression() {
        // expr = (x*x + 3*x) * (x + 2)
        Expression expr = Expression.multiply(
                Expression.add(
                        Expression.multiply(Expression.make("x"), Expression.make("x")),
                        Expression.multiply(Expression.make(3), Expression.make("x"))
                ),
                Expression.add(Expression.make("x"), Expression.make(2))
        );

        Expression derivative = expr.differentiate("x");
        assertNotNull(derivative);

        Expression u = Expression.add(
                Expression.multiply(Expression.make("x"), Expression.make("x")),
                Expression.multiply(Expression.make(3), Expression.make("x"))
        );
        Expression v = Expression.add(Expression.make("x"), Expression.make(2));

        Expression uPrime = Expression.add(
                Expression.add(
                        Expression.multiply(Expression.make("x"), Expression.make(1)),
                        Expression.multiply(Expression.make("x"), Expression.make(1))
                ),
                Expression.add(
                        Expression.multiply(Expression.make(3), Expression.make(1)),
                        Expression.multiply(Expression.make("x"), Expression.make(0))
                )
        );

        Expression vPrime = Expression.add(Expression.make(1), Expression.make(0));

        Expression expected = Expression.add(
                Expression.multiply(u, vPrime),
                Expression.multiply(v, uPrime)
        );

        assertEquals(expected, derivative);
    }
}
