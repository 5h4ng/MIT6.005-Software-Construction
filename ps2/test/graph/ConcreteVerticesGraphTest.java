/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Tests for ConcreteVerticesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph();
    }
    
    /*
     * Testing ConcreteVerticesGraph...
     */
    
    // Testing strategy for ConcreteVerticesGraph.toString()
    
    // tests for ConcreteVerticesGraph.toString()
    @Test
    public void testToStringEmptyGraph() {
        Graph<String> graph = new ConcreteVerticesGraph();
        assertEquals("", graph.toString());
    }

    @Test
    public void testToStringSingleVertexNoEdge() {
        Graph<String> graph = new ConcreteVerticesGraph();
        graph.add("A");
        assertEquals("", graph.toString()); // 没有边，不输出
    }

    @Test
    public void testToStringSingleEdge() {
        Graph<String> graph = new ConcreteVerticesGraph();
        graph.set("A", "B", 5);
        String expected = "(A) --[5]--> (B)\n";
        assertEquals(expected, graph.toString());
    }

    @Test
    public void testToStringMultipleEdges() {
        Graph<String> graph = new ConcreteVerticesGraph();
        graph.set("A", "B", 5);
        graph.set("A", "C", 7);
        String result = graph.toString();
        assertTrue(result.contains("(A) --[5]--> (B)\n"));
        assertTrue(result.contains("(A) --[7]--> (C)\n"));
    }

    @Test
    public void testToStringMultipleVerticesAndEdges() {
        Graph<String> graph = new ConcreteVerticesGraph();
        graph.set("A", "B", 5);
        graph.set("A", "C", 7);
        graph.set("B", "C", 2);
        String result = graph.toString();
        assertTrue(result.contains("(A) --[5]--> (B)\n"));
        assertTrue(result.contains("(A) --[7]--> (C)\n"));
        assertTrue(result.contains("(B) --[2]--> (C)\n"));
    }
    
    /*
     * Testing Vertex...
     */
    
    // Testing strategy for Vertex
    //      Test creating a vertex with a given label.
    //      Test adding a new edge.
    //      Test updating the weight of an existing edge.
    //      Test removing an edge by setting its weight to zero.
    //      Test getting all targets and edge weights.
    //      Test the string representation with no edges and with multiple edges.

    @Test
    public void testConstructorAndGetLabel() {
        Vertex v = new Vertex("A");
        assertEquals("A", v.getLabel());
    }

    @Test
    public void testSetEdgeAddNewEdge() {
        Vertex v = new Vertex("A");
        int prev = v.setEdge("B", 5);
        assertEquals(0, prev);
        assertEquals(5, (int) v.getEdges().get("B"));
    }

    @Test
    public void testSetEdgeUpdateEdge() {
        Vertex v = new Vertex("A");
        v.setEdge("B", 5);
        int prev = v.setEdge("B", 7);
        assertEquals(5, prev);
        assertEquals(7, (int) v.getEdges().get("B"));
    }

    @Test
    public void testSetEdgeRemoveEdge() {
        Vertex v = new Vertex("A");
        v.setEdge("B", 5);
        int prev = v.setEdge("B", 0);
        assertEquals(5, prev);
        assertFalse(v.getEdges().containsKey("B"));
    }

    @Test
    public void testSetEdgeRemoveNonexistentEdge() {
        Vertex v = new Vertex("A");
        int prev = v.setEdge("B", 0);
        assertEquals(0, prev);
    }

    @Test
    public void testGetTargets() {
        Vertex v = new Vertex("A");
        v.setEdge("B", 2);
        v.setEdge("C", 3);
        Set<String> targets = v.getTargets();
        assertTrue(targets.contains("B"));
        assertTrue(targets.contains("C"));
        assertEquals(2, targets.size());
    }

    @Test
    public void testGetEdgesDefensiveCopy() {
        Vertex v = new Vertex("A");
        v.setEdge("B", 1);
        Map<String, Integer> edges = v.getEdges();
        edges.put("C", 2);
        assertFalse(v.getEdges().containsKey("C"));
    }
    @Test
    public void testVertexToString() {
        Vertex v = new Vertex("A");
        v.setEdge("B", 3);
        v.setEdge("C", 6);
        String s = v.toString();
        assertTrue(s.contains("(A) --[3]--> (B)\n"));
        assertTrue(s.contains("(A) --[6]--> (C)\n"));
    }
}
