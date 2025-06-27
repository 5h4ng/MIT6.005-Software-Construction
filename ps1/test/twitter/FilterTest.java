/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy for Filter methods:
     * 
     * writtenBy():
     * Partitions for tweets list: empty, single tweet, multiple tweets
     * Partitions for username: exact match (case insensitive), no match, multiple matches
     * Partitions for case sensitivity: same case, different case (upper/lower)
     * 
     * inTimespan():
     * Partitions for tweets list: empty, single tweet, multiple tweets
     * Partitions for timespan coverage: no tweets in range, some tweets in range, all tweets in range
     * Partitions for boundary conditions: tweets exactly at start/end boundaries, before start, after end
     * 
     * containing():
     * Partitions for tweets list: empty, single tweet, multiple tweets
     * Partitions for words list: empty, single word, multiple words
     * Partitions for word matching: exact match, case insensitive match, partial word match, no match
     * Partitions for word boundaries: word at start/middle/end of tweet, punctuation adjacent
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T13:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "ALYSSA", "Another tweet by alyssa with DIFFERENT case", d3);
    private static final Tweet tweet4 = new Tweet(4, "charlie", "No relevant content here at all", d4);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // ========== Tests for writtenBy() ==========
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> writtenBy = Filter.writtenBy(Collections.emptyList(), "alyssa");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "nonexistent");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet3), "alyssa");
        
        assertEquals("expected two tweets", 2, writtenBy.size());
        assertTrue("expected list to contain both tweets", writtenBy.containsAll(Arrays.asList(tweet1, tweet3)));
        assertEquals("expected original order preserved", 0, writtenBy.indexOf(tweet1));
        assertEquals("expected original order preserved", 1, writtenBy.indexOf(tweet3));
    }
    
    @Test
    public void testWrittenByUppercaseUsername() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet3), "ALYSSA");
        
        assertEquals("expected two tweets", 2, writtenBy.size());
        assertTrue("expected list to contain both tweets", writtenBy.containsAll(Arrays.asList(tweet1, tweet3)));
    }
    
    @Test
    public void testWrittenBySingleTweetMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // ========== Tests for inTimespan() ==========
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testInTimespanEmptyList() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Collections.emptyList(), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanNoTweetsInRange() {
        Instant testStart = Instant.parse("2016-02-17T08:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanBoundaryExclusive() {
        // Test that tweets exactly at start and end boundaries are excluded
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(d1, d2));
        
        assertTrue("expected empty list since boundaries are exclusive", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanSingleTweetInRange() {
        Instant testStart = Instant.parse("2016-02-17T09:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton list", 1, inTimespan.size());
        assertTrue("expected list to contain tweet1", inTimespan.contains(tweet1));
    }
    
    @Test
    public void testInTimespanOrderPreserved() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T14:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet4, tweet1, tweet3, tweet2), new Timespan(testStart, testEnd));
        
        // Should preserve original order: tweet4, tweet1, tweet3, tweet2 (all should be included)
        assertEquals("expected all tweets", 4, inTimespan.size());
        assertEquals("expected original order preserved", 0, inTimespan.indexOf(tweet4));
        assertEquals("expected original order preserved", 1, inTimespan.indexOf(tweet1));
        assertEquals("expected original order preserved", 2, inTimespan.indexOf(tweet3));
        assertEquals("expected original order preserved", 3, inTimespan.indexOf(tweet2));
    }
    
    // ========== Tests for containing() ==========
    
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    @Test
    public void testContainingEmptyTweetsList() {
        List<Tweet> containing = Filter.containing(Collections.emptyList(), Arrays.asList("talk"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    @Test
    public void testContainingEmptyWordsList() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Collections.emptyList());
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    @Test
    public void testContainingNoMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("nonexistent"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("TALK"));
        
        assertEquals("expected two tweets", 2, containing.size());
        assertTrue("expected list to contain both tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
    }
    
    @Test
    public void testContainingMultipleWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet4), Arrays.asList("talk", "content"));
        
        assertEquals("expected three tweets", 3, containing.size());
        assertTrue("expected list to contain matching tweets", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet4)));
    }
    
    @Test
    public void testContainingSingleWordSingleTweet() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet2), Arrays.asList("hype"));
        
        assertEquals("expected singleton list", 1, containing.size());
        assertTrue("expected list to contain tweet2", containing.contains(tweet2));
    }
    
    @Test
    public void testContainingPartialWordMatch() {
        // Test that partial matches within words work (based on contains() behavior)
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("reason"));
        
        assertEquals("expected singleton list", 1, containing.size());
        assertTrue("expected list to contain tweet1", containing.contains(tweet1));
    }
    
    @Test
    public void testContainingOrderPreserved() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet4, tweet1, tweet2), Arrays.asList("talk"));
        
        // Should preserve original order: tweet1, tweet2 (tweet4 doesn't contain "talk")
        assertEquals("expected two tweets", 2, containing.size());
        assertEquals("expected original order preserved", 0, containing.indexOf(tweet1));
        assertEquals("expected original order preserved", 1, containing.indexOf(tweet2));
    }
    
    @Test
    public void testContainingDuplicateAvoidance() {
        // Tweet should appear only once even if it matches multiple words
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk", "reasonable"));
        
        assertEquals("expected singleton list", 1, containing.size());
        assertTrue("expected list to contain tweet1", containing.contains(tweet1));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
