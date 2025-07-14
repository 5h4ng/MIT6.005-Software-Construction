/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {
    
    private final Graph<String> graph = Graph.empty();
    
    // Abstraction function:
    //   graph represents the word affinity graph deriving from a corpus of text.
    //      - Each vertex is a word (lowercase, as defined above)
    //      - There is a directed edge from w1 to w2 with weight n if w1 is immediately followed by w2 n times in the corpus
    // Representation invariant:
    //      - All vertices are non-empty, lowercase, non-space, non-newline strings
    //      - All edge weights >= 1
    // Safety from rep exposure:
    //   All the fields are final and private.
    
    /**
     * Create a new poet with the graph from corpus (as described above).
     * 
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
        StringBuilder input = new StringBuilder();
        List<String> lines = Files.readAllLines(corpus.toPath());
        for (String line : lines) {
            input.append(line).append(" ");
        }
        String text = input.toString().trim();
        String[] words = text.split("\\s+");

        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i].toLowerCase();
            String w2 = words[i + 1].toLowerCase();
            graph.add(w1);
            graph.add(w2);
            int existingWeight = graph.targets(w1).getOrDefault(w2, 0);
            graph.set(w1, w2, existingWeight + 1);
        }
        checkRep();
    }
    
    // checkRep
    private void checkRep() {
        for (String vertex : graph.vertices()) {
            assert vertex.equals(vertex.toLowerCase());
            assert !vertex.contains(" ");
            assert !vertex.contains("\n");
        }
        for (String vertex1 : graph.vertices()) {
            for (String vertex2 : graph.vertices()) {
                if (graph.targets(vertex1).containsKey(vertex2)) {
                    int weight = graph.targets(vertex1).get(vertex2);
                    assert weight >= 1;
                }
            }
        }
    }
    
    /**
     * Generate a poem.
     * 
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();
        result.append(words[0]);
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i].toLowerCase();
            String w2 = words[i + 1].toLowerCase();
            String bestBridge = "";
            int maxWeight = -1;

            for (String b : graph.vertices()) {
                if (graph.targets(w1).containsKey(b) && graph.targets(b).containsKey(w2)) {
                    Integer w1b = graph.targets(w1).get(b);
                    Integer bw2 = graph.targets(b).get(w2);
                    int weight = w1b + bw2;
                    if (weight > maxWeight) {
                        maxWeight = weight;
                        bestBridge = b;
                    }
                }
            }

            if (!bestBridge.isEmpty()) {
                result.append(" ").append(bestBridge);
            }
            result.append(" ").append(words[i + 1]);
        }
        return result.toString();
    }
    
    // toString()
    public String toString() {
        return graph.toString();
    }
    
}
