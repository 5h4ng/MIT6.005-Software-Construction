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
public class ConcreteEdgesGraph<L> implements Graph<L> {
    
    private final Set<L> vertices = new HashSet<>();
    private final List<Edge<L>> edges = new ArrayList<>();
    
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
        Set<L> edgeVertices = new HashSet<>();
        for (Edge<L> edge : edges) {
            assert edge.getWeight() > 0;
            edgeVertices.add(edge.getSource());
            edgeVertices.add(edge.getTarget());
        }
        assert vertices.containsAll(edgeVertices);
    }
    
    @Override
    public boolean add(L vertex) {
        if (vertices.contains(vertex)) {
            return false;
        }
        vertices.add(vertex);
        checkRep();
        return true;
    }
    
    @Override
    public int set(L source, L target, int weight) {
        int previousWeight = 0;
        // find the existing edge
        Edge<L> foundEdge = null;
        for (Edge<L> edge : edges) {
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
            edges.add(new Edge<>(source, target, weight));
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
    public boolean remove(L vertex) {
        if (!vertices.contains(vertex)) {
            return false;
        }
        vertices.remove(vertex);
        Edge<L> foundEdge = null;
        for (Edge<L> edge : edges) {
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
    public Set<L> vertices() {
        // defensive copying
        return new HashSet<>(vertices);
    }
    
    @Override
    public Map<L, Integer> sources(L target) {
        Map<L, Integer> results = new HashMap<>();
        for (Edge<L> edge : edges) {
            if (edge.getTarget().equals(target)) {
                results.put(edge.getSource(), edge.getWeight());
            }
        }
        return results;
    }
    
    @Override
    public Map<L, Integer> targets(L source) {
        Map<L, Integer> results = new HashMap<>();
        for (Edge<L> edge : edges) {
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
        for (Edge<L> edge : edges) {
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
class Edge<L> {
    private final L source;
    private final L target;
    private final int weight;

    // Abstraction function:
    //      represents a weighted directed edge.
    // Representation invariant:
    //      Weight is greater than zero.
    // Safety from rep exposure:
    //      All fields are private and final.
    
    // constructor
    public Edge(L source, L target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        checkRep();
    }

    // checkRep
    private void checkRep() {
        assert weight > 0;
    }
    
    public L getSource() {
        return source;
    }

    public L getTarget() {
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
