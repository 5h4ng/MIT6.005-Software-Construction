/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    // testing strategy for getMentionedUsers:
    // partitions:
    // - tweet with no @
    // - tweet with one @user (start/middle/end)
    // - tweet with multiple @user
    // - tweet with @ before/after username character (invalid)
    // - tweet with @ at start/end
    // - tweet with @user, different cases, repeated mention
    // - tweet with @ in email
    // - multiple tweets, overlap and distinct users

    @Test
    public void testNoMention() {
        Tweet t = new Tweet(1, "user", "hello world", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSingleMention() {
        Tweet t = new Tweet(1, "user", "hi @alice", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertEquals(Collections.singleton("alice"), toLowerCaseSet(result));
    }

    @Test
    public void testMultipleMentions() {
        Tweet t = new Tweet(1, "user", "hi @alice and @bob!", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertEquals(new HashSet<>(Arrays.asList("alice", "bob")), toLowerCaseSet(result));
    }

    @Test
    public void testMentionWithPunctuation() {
        Tweet t = new Tweet(1, "user", "hello, @alice! Are you there?", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertEquals(Collections.singleton("alice"), toLowerCaseSet(result));
    }

    @Test
    public void testMentionAtStartAndEnd() {
        Tweet t1 = new Tweet(1, "user", "@alice how are you?", d1);
        Tweet t2 = new Tweet(2, "user", "see you later @bob", d2);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        assertEquals(new HashSet<>(Arrays.asList("alice", "bob")), toLowerCaseSet(result));
    }

    @Test
    public void testMentionWithFollowingUsernameChar() {
        Tweet t = new Tweet(1, "user", "hi @alice1more", d1); // should match alice1more as one user
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertEquals(Collections.singleton("alice1more"), toLowerCaseSet(result));
    }

    @Test
    public void testAtPrecededByUsernameChar() {
        Tweet t = new Tweet(1, "user", "foo@bar, hi@alice, my_email@mit.edu", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAtWithNoUsername() {
        Tweet t = new Tweet(1, "user", "hi @ there", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMentionCaseInsensitiveAndDuplicates() {
        Tweet t = new Tweet(1, "user", "@Alice @ALICE @alice", d1);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t));
        // Twitter usernames are case-insensitive, so only one "alice"
        assertEquals(Collections.singleton("alice"), toLowerCaseSet(result));
    }

    @Test
    public void testMultipleTweetsOverlappingUsers() {
        Tweet t1 = new Tweet(1, "user", "hi @alice", d1);
        Tweet t2 = new Tweet(2, "user", "hello @bob and @Alice", d2);
        Tweet t3 = new Tweet(3, "user", "see @bob", d3);
        Set<String> result = Extract.getMentionedUsers(Arrays.asList(t1, t2, t3));
        assertEquals(new HashSet<>(Arrays.asList("alice", "bob")), toLowerCaseSet(result));
    }

    /** helper to lowercase all in a set, for case-insensitive checking */
    private Set<String> toLowerCaseSet(Set<String> set) {
        Set<String> lower = new HashSet<>();
        for (String s : set) {
            lower.add(s.toLowerCase());
        }
        return lower;
    }
}

