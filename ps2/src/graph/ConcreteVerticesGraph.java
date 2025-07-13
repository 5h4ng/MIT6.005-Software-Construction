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
public class ConcreteVerticesGraph<L> implements Graph<L> {
    
    private final List<Vertex<L>> vertices = new ArrayList<>();
    
    // Abstraction function:
    //      represents the weighted directed graph.
    // Representation invariant:
    //      There is no redundant element in the list of vertices.
    //      The weight of an edge must be positive.
    // Safety from rep exposure:
    //      All fields are private and final.
    //      Vertex is immutable.
    
    // constructor
    public ConcreteVerticesGraph() {}
    
    // checkRep
    private void checkRep() {
        Set<L> VerticesLabels = new HashSet<>();
        for (Vertex<L> vertex : vertices) {
            VerticesLabels.add(vertex.getLabel());
        }
        assert VerticesLabels.size() == vertices.size();
    }
    
    @Override public boolean add(L vertex) {
        for (Vertex<L> vertex1 : vertices) {
            if (vertex1.getLabel().equals(vertex)) {
                return false;
            }
        }
        vertices.add(new Vertex<>(vertex));
        return true;
    }

    @Override
    public int set(L source, L target, int weight) {
        // find source
        boolean sourceExists = false;
        Vertex<L> sourceVertex = null;
        for (Vertex<L>vertex1 : vertices) {
            if (vertex1.getLabel().equals(source)) {
                sourceExists = true;
                sourceVertex = vertex1;
                break;
            }
        }
        // if weight > 0, and source does not exist, create the source vertex
        if (!sourceExists && weight > 0) {
            sourceVertex = new Vertex<>(source);
            vertices.add(sourceVertex);
            sourceExists = true;
        }
        // 3. find targets
        boolean targetExists = false;
        for (Vertex<L> vertex1 : vertices) {
            if (vertex1.getLabel().equals(target)) {
                targetExists = true;
                break;
            }
        }
        // 4. if weight > 0, and target doesn't exist, create it
        if (!targetExists && weight > 0) {
            vertices.add(new Vertex<>(target));
        }
        // 5. operate the edge
        if (sourceExists) {
            int prev = sourceVertex.setEdge(target, weight);
            checkRep();
            return prev;
        } else {
            return 0; // source does not exit
        }
    }

    
    @Override public boolean remove(L vertex) {
        for (Vertex<L> vertex1 : vertices) {
            if (vertex1.getLabel().equals(vertex)) {
                vertices.remove(vertex1);
                return true;
            }
        }
        return false;
    }
    
    @Override public Set<L> vertices() {
        Set<L> verticesLabels = new HashSet<>();
        for (Vertex<L> vertex : vertices) {
            verticesLabels.add(vertex.getLabel());
        }
        return verticesLabels;
    }
    
    @Override public Map<L, Integer> sources(L target) {
       Map<L, Integer> sources = new HashMap<>();
        for (Vertex<L> vertex : vertices) {
            Map<L, Integer> outEdges = vertex.getEdges();
            for (L target1 : outEdges.keySet()) {
                if (target1.equals(target)) {
                    sources.put(vertex.getLabel(), outEdges.get(target1));
                }
            }
        }
        return sources;
    }
    
    @Override
    public Map<L, Integer> targets(L source) {
        Map<L, Integer> targets = new HashMap<>();
        for (Vertex<L> vertex : vertices) {
            if (vertex.getLabel().equals(source)) {
                targets.putAll(vertex.getEdges());
            }
        }
        return targets;
    }
    
    // toString()
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Vertex<L> vertex : vertices) {
            output.append(vertex.toString());
        }
        return output.toString();
    }
    
}

/**
 *
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex<L> {

    private final L label;
    private final Map<L, Integer> targets = new HashMap<>();

    // Abstraction function:
    //      represents a vertex named 'label' with all the targets and the edge weights stored in 'targets'.
    // Representation invariant:
    //      weight > 0
    // Safety from rep exposure:
    //      All fields are private and final.
    
    // constructor
    public Vertex(L label, Map<L, Integer> targets) {
        this.label = label;
        this.targets.putAll(targets);
    }

    public Vertex(L label) {
        this.label = label;
    }

    // checkRep
    private void checkRep() {
        for (L target : targets.keySet()) {
            assert targets.get(target) > 0;
        }
    }
    
    // methods
    public L getLabel() {
        return label;
    }

    public Set<L> getTargets() {
        return targets.keySet();
    }

    /**
     * Gets the edges that starts from the vertex.
     * @return the edges
     */
    public Map<L, Integer> getEdges() {
        // defensive copying
        return new HashMap<>(targets);
    }

    /**
     * If weight is nonzero, add an edge or update the weight of that edge;
     * vertices with the given labels are added to the graph if they do not already exist.
     * If weight is zero, remove the edge if it exists (the graph is not otherwise modified).
     * @param target the label of target vertex
     * @param weight the weight of the edge
     * @return the previous weight of the edge, or zero if there was no such edge
     */
    public int setEdge(L target, int weight) {
        if (weight == 0) {
            if (targets.containsKey(target)) {
                int prev = targets.get(target);
                targets.remove(target);
                return prev;
            }
            return 0;
        } else {
            if (targets.containsKey(target)) {
                int prev = targets.get(target);
                targets.put(target, weight);
                checkRep();
                return prev;
            } else {
                targets.put(target, weight);
                checkRep();
                return 0;
            }
        }
    }

    public boolean removeTarget(L target) {
        if (targets.containsKey(target)) {
            targets.remove(target);
            return true;
        }
        return false;
    }

    // toString()
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (L target : targets.keySet()) {
            output.append("(").append(label).append(") --[").append(targets.get(target)).append("]--> (").append(target).append(")\n");
        }
        return output.toString();
    }
}
