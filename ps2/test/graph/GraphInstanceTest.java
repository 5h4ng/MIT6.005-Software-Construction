/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for instance methods of Graph.
 * 
 * <p>PS2 instructions: you MUST NOT add constructors, fields, or non-@Test
 * methods to this class, or change the spec of {@link #emptyInstance()}.
 * Your tests MUST only obtain Graph instances by calling emptyInstance().
 * Your tests MUST NOT refer to specific concrete implementations.
 */
public abstract class GraphInstanceTest {
    
    // Testing strategy
    //  add(): add new vertex, and add duplicate vertex
    //  set(): add edge, update edge weight, remove edge (set the weight zero),
    //      self-loop
    //  remove(): remove existing vertex (with/without edges), remove non-existing
    //      vertex
    //  vertices(): empty, after add, after remove
    //  sources()/targets(): after add, remove, query for non-existent vertex
    
    /**
     * Overridden by implementation-specific test classes.
     * 
     * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testInitialVerticesEmpty() {
        // TODO you may use, change, or remove this test
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }
    
    // TODO other tests for instance methods of Graph
    @Test
    public void testAddVertex() {
        Graph<String> g =  emptyInstance();
        assertTrue(g.add("a"));
        assertTrue(g.add("b"));
        assertFalse(g.add("a"));
        assertTrue(g.vertices().contains("a"));
        assertTrue(g.vertices().contains("b"));
    }

    @Test
    public void testSet() {
        Graph<String> g =  emptyInstance();
        // set with non-existing vertices
        assertEquals(0, g.set("a", "b", 5));
        assertTrue(g.vertices().contains("a"));
        assertTrue(g.vertices().contains("b"));
        assertEquals(Integer.valueOf(5), g.targets("a").get("b"));
        // update edge weight
        assertEquals(5, g.set("a", "b", 10));
        assertEquals(Integer.valueOf(10), g.targets("a").get("b"));
        // remove edge
        assertEquals(10, g.set("a", "b", 0));
        assertFalse(g.targets("a").containsKey("b"));
    }

    @Test
    public void testRemoveVertex() {
        Graph<String> g = emptyInstance();
        g.add("a");
        g.add("b");
        g.set("a", "b", 2);
        assertTrue(g.remove("a"));
        assertFalse(g.vertices().contains("a"));
        // edge should be removed as well
        assertFalse(g.sources("b").containsKey("a"));
        assertFalse(g.remove("a")); // remove non-existing
    }

    @Test
    public void testSourcesAndTargets() {
        Graph<String> g = emptyInstance();
        g.set("a", "b", 3);
        g.set("c", "b", 7);
        assertEquals(Integer.valueOf(3), g.sources("b").get("a"));
        assertEquals(Integer.valueOf(7), g.sources("b").get("c"));
        assertEquals(Integer.valueOf(3), g.targets("a").get("b"));
        assertTrue(g.sources("x").isEmpty()); // x is non-existent
        assertTrue(g.targets("x").isEmpty()); // x is non-existent
    }

    @Test
    public void testSelfLoopEdge() {
        Graph<String> g = emptyInstance();
        g.set("a", "a", 9);
        assertEquals(Integer.valueOf(9), g.sources("a").get("a"));
        assertEquals(Integer.valueOf(9), g.targets("a").get("a"));
    }
    
}
