/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Map;

/**
 * Tests for the static methods of Commands.
 */
public class CommandsTest {
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // ---------------- differentiate() tests ----------------

    @Test
    public void testDifferentiateConstant() {
        String derivative = Commands.differentiate("5", "x");
        assertEquals("0.0", derivative); // derivative of constant = 0
    }

    @Test
    public void testDifferentiateVariable() {
        String derivative = Commands.differentiate("x", "x");
        assertEquals("1.0", derivative);
    }

    @Test
    public void testDifferentiateVariableOther() {
        String derivative = Commands.differentiate("y", "x");
        assertEquals("0.0", derivative);
    }

    @Test
    public void testDifferentiateSimpleAddition() {
        String derivative = Commands.differentiate("x + 3", "x");
        // d/dx (x+3) = 1+0
        assertEquals("(1.0 + 0.0)", derivative);
    }

    @Test
    public void testDifferentiateSimpleMultiplication() {
        String derivative = Commands.differentiate("x * x", "x");
        // d/dx (x*x) = (x*1) + (x*1)
        assertEquals("((x * 1.0) + (x * 1.0))", derivative);
    }

    // ---------------- simplify() tests ----------------
    @Test
    public void testSimplifyConstant() {
        String result = Commands.simplify("2*3+4", Map.of());
        assertEquals("10.0", result);
    }

    @Test
    public void testSimplifyVariableNotInEnv() {
        String result = Commands.simplify("x+2", Map.of());
        assertEquals("(x + 2.0)", result);
    }

    @Test
    public void testSimplifyVariableInEnv() {
        String result = Commands.simplify("x+2", Map.of("x", 3.0));
        // 3+2 = 5
        assertEquals("5.0", result);
    }

    @Test
    public void testSimplifyMultipleVariablesPartial() {
        String result = Commands.simplify("x*x + y", Map.of("x", 2.0));
        // expect 4+y
        assertEquals("(4.0 + y)", result);
    }

    @Test
    public void testSimplifyAllVariables() {
        String result = Commands.simplify("x*y", Map.of("x", 2.0, "y", 4.0));
        assertEquals("8.0", result);
    }
    
}
