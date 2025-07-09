/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.*;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph implements Graph<String> {
    
    private final Set<String> vertices = new HashSet<>();
    private final List<Edge> edges = new ArrayList<>();
    
    // Abstraction function:
    //      represents the weighted directed graph.
    // Representation invariant:
    //      Every target and source of the edge in the list must in the set of vertices.
    //      The weight of an edge must be a positive integer.
    //      There is only one edge between two vertices.
    // Safety from rep exposure:
    //      All fields are private and final;
    //      Use defensive copying for mutable fields.
    //      Edge is immutable.

    // constructor
    public ConcreteEdgesGraph() {}

    private void checkRep() {
        Set<String> edgeVertices = new HashSet<>();
        for (Edge edge : edges) {
            assert edge.getWeight() > 0;
            edgeVertices.add(edge.getSource());
            edgeVertices.add(edge.getTarget());
        }
        assert vertices.containsAll(edgeVertices);
    }
    
    @Override
    public boolean add(String vertex) {
        if (vertices.contains(vertex)) {
            return false;
        }
        vertices.add(vertex);
        checkRep();
        return true;
    }
    
    @Override
    public int set(String source, String target, int weight) {
        int previousWeight = 0;
        // find the existing edge
        Edge foundEdge = null;
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                foundEdge = edge;
                break;
            }
        }
        if (weight > 0) {
            vertices.add(source);
            vertices.add(target);
            if (foundEdge != null) {
                // update weight
                previousWeight = foundEdge.getWeight();
                edges.remove(foundEdge);
            }
            // add new edge
            edges.add(new Edge(source, target, weight));
        } else {
            // weight == 0，remove the edge
            if (foundEdge != null) {
                previousWeight = foundEdge.getWeight();
                edges.remove(foundEdge);
            }
        }
        checkRep();
        return previousWeight;
    }
    
    @Override
    public boolean remove(String vertex) {
        if (!vertices.contains(vertex)) {
            return false;
        }
        vertices.remove(vertex);
        Edge foundEdge = null;
        for (Edge edge : edges) {
            if (edge.getSource().equals(vertex) || edge.getTarget().equals(vertex)) {
                foundEdge = edge;
            }
        }
        if (foundEdge != null) {
            edges.remove(foundEdge);
        }
        checkRep();
        return true;
    }
    
    @Override
    public Set<String> vertices() {
        // defensive copying
        return new HashSet<>(vertices);
    }
    
    @Override
    public Map<String, Integer> sources(String target) {
        Map<String, Integer> results = new HashMap<>();
        for (Edge edge : edges) {
            if (edge.getTarget().equals(target)) {
                results.put(edge.getSource(), edge.getWeight());
            }
        }
        return results;
    }
    
    @Override
    public Map<String, Integer> targets(String source) {
        Map<String, Integer> results = new HashMap<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(source)) {
                results.put(edge.getTarget(), edge.getWeight());
            }
        }
        return results;
    }

    @Override
    public String toString() {
        // Use StringBuilder to efficiently concatenate strings
        StringBuilder output = new StringBuilder();
        for (Edge edge : edges) {
            output.append(edge.toString());
            output.append("\n");
        }
        return output.toString();
    }
    
}

/**
 * TODO specification
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge {
    private final String source;
    private final String target;
    private final int weight;

    // Abstraction function:
    //      represents a weighted directed edge.
    // Representation invariant:
    //      Weight is greater than zero.
    // Safety from rep exposure:
    //      All fields are private and final.
    
    // constructor
    public Edge(String source, String target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        checkRep();
    }

    // checkRep
    private void checkRep() {
        assert weight > 0;
    }
    
    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }
    
    // toString()
    @Override
    public String toString() {
        return "(" + source + ") --[" + weight + "]--> (" + target + ")";
    }
    
}
