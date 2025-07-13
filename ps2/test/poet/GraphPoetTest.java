/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {
    
    // Testing strategy
    //   - Corpus size: empty corpus, single line, multiple lines
    //   - Input: single word, two words, multiple words, words not in corpus
    //   - Bridge word cases:
    //       - No bridge word possible
    //       - Unique bridge word (with highest weight)
    //       - Multiple possible bridge words (same weight)
    //   - Words with different capitalization
    //   - Words with punctuation
    //   - Corpus file not found (exception)
    //   - Output formatting: correct whitespace, original case, bridge words lowercase
    //
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // tests
    // Corpus: "a b c\nb a b"
    @Test
    public void testNoBridgeWord() throws IOException {
        GraphPoet poet = new GraphPoet(new File("test/poet/mini.txt"));
        String input = "hello world";
        String expected = "hello world";
        assertEquals(expected, poet.poem(input));
    }

    @Test
    public void testSimpleBridgeWord() throws IOException {
        GraphPoet poet = new GraphPoet(new File("test/poet/mini.txt"));
        // a->b = 2, b->c = 1, b->a = 1
        String input = "a c";
        String expected = "a b c";
        assertEquals(expected, poet.poem(input));
    }

    @Test
    public void testMugarOmniTheaterExample() throws IOException {
        GraphPoet poet = new GraphPoet(new File("test/poet/mugar.txt"));
        String input = "Test the system.";
        String expected = "Test of the system.";
        assertEquals(expected, poet.poem(input));
    }

    @Test
    public void testMultipleBridgeWordsWithSameWeight() throws IOException {
        // Create a temporary corpus: "a b c\na d c"
        File corpus = File.createTempFile("corpus", ".txt");
        java.nio.file.Files.write(corpus.toPath(), "a b c\na d c".getBytes());
        GraphPoet poet = new GraphPoet(corpus);
        String input = "a c";
        String output = poet.poem(input);
        // Either "a b c" or "a d c" is acceptable
        assertTrue(output.equals("a b c") || output.equals("a d c"));
        corpus.delete();
    }

    @Test
    public void testCaseInsensitiveBridgeWords() throws IOException {
        // Corpus: "Hello, HELLO, hello, goodbye!"
        File corpus = File.createTempFile("corpus", ".txt");
        java.nio.file.Files.write(corpus.toPath(), "Hello, HELLO, hello, goodbye!".getBytes());
        GraphPoet poet = new GraphPoet(corpus);
        String input = "hello, goodbye!";
        String expected = "hello, goodbye!";
        assertEquals(expected, poet.poem(input));
        corpus.delete();
    }

    @Test
    public void testInputWordsNotInCorpus() throws IOException {
        File corpus = File.createTempFile("corpus", ".txt");
        java.nio.file.Files.write(corpus.toPath(), "a b c".getBytes());
        GraphPoet poet = new GraphPoet(corpus);
        String input = "x y";
        String expected = "x y";
        assertEquals(expected, poet.poem(input));
        corpus.delete();
    }

    @Test(expected=IOException.class)
    public void testCorpusFileNotFound() throws IOException {
        new GraphPoet(new File("test/poet/not_exist.txt"));
    }

    @Test
    public void testOriginalCaseAndBridgeWordLowercase() throws IOException {
        File corpus = File.createTempFile("corpus", ".txt");
        java.nio.file.Files.write(corpus.toPath(), "A B C".getBytes());
        GraphPoet poet = new GraphPoet(corpus);
        String input = "A C";
        // Input retains "A" and "C" case, bridge word is lowercase
        String expected = "A b C";
        assertEquals(expected, poet.poem(input));
        corpus.delete();
    }

    @Test
    public void testWhitespaceBetweenWords() throws IOException {
        File corpus = File.createTempFile("corpus", ".txt");
        java.nio.file.Files.write(corpus.toPath(), "a b c".getBytes());
        GraphPoet poet = new GraphPoet(corpus);
        String input = "a      c";
        // Whitespace between words in output should be a single space
        String expected = "a b c";
        assertEquals(expected, poet.poem(input));
        corpus.delete();
    }
}
