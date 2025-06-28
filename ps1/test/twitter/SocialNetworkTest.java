/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy for SocialNetwork methods:
     * 
     * guessFollowsGraph():
     * Partitions for tweets list: empty, single tweet, multiple tweets
     * Partitions for @-mentions: no mentions, single mention, multiple mentions, self-mention
     * Partitions for users: single user, multiple users, case variations
     * Partitions for duplicate mentions: same user mentioned multiple times, different tweets mentioning same user
     * 
     * influencers():
     * Partitions for followsGraph: empty, single user, multiple users
     * Partitions for follower counts: no followers, single follower, multiple followers, tied follower counts
     * Partitions for users: users who follow others, users who are followed, users who both follow and are followed
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alice", "Hey @bob, how are you?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bob", "Thanks @alice! Also hi @charlie", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "Hello everyone!", d3);
    private static final Tweet tweet4 = new Tweet(4, "alice", "Meeting with @BOB and @Charlie tomorrow", d1);
    private static final Tweet tweet5 = new Tweet(5, "david", "I love @alice's work", d2);
    private static final Tweet tweet6 = new Tweet(6, "eve", "Check out alice@example.com - not a mention!", d3);
    private static final Tweet tweet7 = new Tweet(7, "frank", "No mentions here", d1);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // ========== Tests for guessFollowsGraph() ==========
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet7));
        
        // Authors should appear in graph even if they don't follow anyone
        assertTrue("expected graph to contain charlie or be empty for charlie", 
                   !followsGraph.containsKey("charlie") || followsGraph.get("charlie").isEmpty());
        assertTrue("expected graph to contain frank or be empty for frank", 
                   !followsGraph.containsKey("frank") || followsGraph.get("frank").isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        
        // Alice mentions Bob, so Alice should follow Bob
        assertTrue("expected alice to be in graph", followsGraph.containsKey("alice"));
        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
        assertEquals("expected alice to follow exactly one person", 1, followsGraph.get("alice").size());
    }
    
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
        
        // Bob mentions both Alice and Charlie
        assertTrue("expected bob to be in graph", followsGraph.containsKey("bob"));
        assertTrue("expected bob to follow alice", followsGraph.get("bob").contains("alice"));
        assertTrue("expected bob to follow charlie", followsGraph.get("bob").contains("charlie"));
        assertEquals("expected bob to follow exactly two people", 2, followsGraph.get("bob").size());
    }
    
    @Test
    public void testGuessFollowsGraphCaseInsensitive() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4));
        
        // Alice mentions @BOB and @Charlie (different cases)
        assertTrue("expected alice to be in graph", followsGraph.containsKey("alice"));
        assertTrue("expected alice to follow bob (case insensitive)", followsGraph.get("alice").contains("bob"));
        assertTrue("expected alice to follow charlie (case insensitive)", followsGraph.get("alice").contains("charlie"));
        assertEquals("expected alice to follow exactly two people", 2, followsGraph.get("alice").size());
    }
    
    @Test
    public void testGuessFollowsGraphMultipleTweets() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2, tweet5));
        
        // Alice mentions Bob, Bob mentions Alice and Charlie, David mentions Alice
        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
        assertTrue("expected bob to follow alice", followsGraph.get("bob").contains("alice"));
        assertTrue("expected bob to follow charlie", followsGraph.get("bob").contains("charlie"));
        assertTrue("expected david to follow alice", followsGraph.get("david").contains("alice"));
    }
    
    @Test
    public void testGuessFollowsGraphNoDuplicateFollows() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet4));
        
        // Alice mentions Bob in both tweets, but should only follow him once
        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
        // Count how many times bob appears in alice's follow set
        long bobCount = followsGraph.get("alice").stream().filter(name -> name.equals("bob")).count();
        assertEquals("expected bob to appear only once in alice's follows", 1, bobCount);
    }
    
    @Test
    public void testGuessFollowsGraphNoSelfFollow() {
        Tweet selfMention = new Tweet(10, "alice", "Hello @alice, talking to myself!", d1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(selfMention));
        
        // Alice should not follow herself
        if (followsGraph.containsKey("alice")) {
            assertFalse("expected alice not to follow herself", followsGraph.get("alice").contains("alice"));
        }
    }
    
    @Test
    public void testGuessFollowsGraphEmailNotMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet6));
        
        // alice@example.com should not be treated as a mention of alice
        if (followsGraph.containsKey("eve")) {
            assertFalse("expected eve not to follow alice from email", followsGraph.get("eve").contains("alice"));
        }
    }
    
    @Test
    public void testGuessFollowsGraphUsernamesFromTweets() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2));
        
        // All usernames in the graph should be from authors or mentions in the tweets
        Set<String> expectedUsers = new HashSet<>(Arrays.asList("alice", "bob", "charlie"));
        Set<String> actualUsers = new HashSet<>();
        
        for (String user : followsGraph.keySet()) {
            actualUsers.add(user);
        }
        for (Set<String> follows : followsGraph.values()) {
            actualUsers.addAll(follows);
        }
        
        assertTrue("all users in graph should be from tweets", expectedUsers.containsAll(actualUsers));
    }
    
    // ========== Tests for influencers() ==========
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    @Test
    public void testInfluencersSingleUser() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected single user", 1, influencers.size());
        assertTrue("expected list to contain alice", influencers.contains("alice"));
    }
    
    @Test
    public void testInfluencersNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        followsGraph.put("bob", new HashSet<>());
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected two users", 2, influencers.size());
        assertTrue("expected list to contain alice", influencers.contains("alice"));
        assertTrue("expected list to contain bob", influencers.contains("bob"));
    }
    
    @Test
    public void testInfluencersDescendingOrder() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Arrays.asList("charlie")));
        followsGraph.put("bob", new HashSet<>(Arrays.asList("charlie")));
        followsGraph.put("dave", new HashSet<>(Arrays.asList("charlie", "alice")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected four users", 4, influencers.size());
        // Charlie has 3 followers, Alice has 1, Bob and Dave have 0
        assertEquals("expected charlie to be most influential", "charlie", influencers.get(0));
        assertEquals("expected alice to be second", "alice", influencers.get(1));
        // Bob and Dave have same follower count, order between them is underdetermined
        assertTrue("expected bob and dave to be last two", 
                  (influencers.get(2).equals("bob") && influencers.get(3).equals("dave")) ||
                  (influencers.get(2).equals("dave") && influencers.get(3).equals("bob")));
    }
    
    @Test
    public void testInfluencersIncludesAllUsers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob")));
        // Charlie is mentioned but doesn't have an entry as a key
        followsGraph.put("dave", new HashSet<>(Arrays.asList("charlie")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected four users", 4, influencers.size());
        assertTrue("expected list to contain alice", influencers.contains("alice"));
        assertTrue("expected list to contain bob", influencers.contains("bob"));
        assertTrue("expected list to contain charlie", influencers.contains("charlie"));
        assertTrue("expected list to contain dave", influencers.contains("dave"));
    }
    
    @Test
    public void testInfluencersDistinctUsers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob")));
        followsGraph.put("charlie", new HashSet<>(Arrays.asList("bob")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        // Bob should appear only once even though followed by multiple people
        long bobCount = influencers.stream().filter(name -> name.equals("bob")).count();
        assertEquals("expected bob to appear only once", 1, bobCount);
    }
    
    @Test
    public void testInfluencersComplexNetwork() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob", "charlie")));
        followsGraph.put("bob", new HashSet<>(Arrays.asList("charlie")));
        followsGraph.put("dave", new HashSet<>(Arrays.asList("alice", "charlie")));
        followsGraph.put("eve", new HashSet<>(Arrays.asList("alice")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        // Charlie: 3 followers (alice, bob, dave)
        // Alice: 2 followers (dave, eve)  
        // Bob: 1 follower (alice)
        // Dave, Eve: 0 followers
        assertEquals("expected five users", 5, influencers.size());
        assertEquals("expected charlie to be most influential", "charlie", influencers.get(0));
        assertEquals("expected alice to be second", "alice", influencers.get(1));
        assertEquals("expected bob to be third", "bob", influencers.get(2));
        // Dave and Eve have same follower count, order between them is underdetermined
        assertTrue("expected dave and eve to be last two",
                  (influencers.get(3).equals("dave") && influencers.get(4).equals("eve")) ||
                  (influencers.get(3).equals("eve") && influencers.get(4).equals("dave")));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
