/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for static methods of Graph.
 * <p>
 * To facilitate testing multiple implementations of Graph, instance methods are
 * tested in GraphInstanceTest.
 */
public class GraphStaticTest {
    
    // Testing strategy
    //   empty()
    //     no inputs, only output is empty graph
    //     observe with vertices()
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testEmptyVerticesEmpty() {
        assertEquals("expected empty() graph to have no vertices",
                Collections.emptySet(), Graph.empty().vertices());
    }

    @Test
    public void testEmptyVerticesWithIntegerLabel() {
        Graph<Integer> graph = Graph.empty();
        assertEquals("expected empty() graph to have no vertices for Integer label",
                Collections.emptySet(), graph.vertices());
    }

    @Test
    public void testEmptyVerticesWithDoubleLabel() {
        Graph<Double> graph = Graph.empty();
        assertEquals("expected empty() graph to have no vertices for Double label",
                Collections.emptySet(), graph.vertices());
    }
    
}
