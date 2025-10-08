package abc.music;

import abc.sound.SequencePlayer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Parallel represents a collection of multiple voices of music that can be played simultaneously.
 * This class is NOT a Music implementation, but rather a container/organizer for multiple Music objects.
 */
public class Parallel {
    private final List<Music> voices;

    /**
     * Make a Parallel.
     * @param voices list of music to play simultaneously, must not be null or empty
     */
    public Parallel(List<Music> voices) {
        if (voices == null || voices.isEmpty()) {
            throw new IllegalArgumentException("voices cannot be null or empty");
        }
        this.voices = new ArrayList<>(voices);
        checkRep();
    }

    /**
     * Make a Parallel with varargs.
     * @param voices music pieces to play simultaneously
     */
    public Parallel(Music... voices) {
        if (voices == null || voices.length == 0) {
            throw new IllegalArgumentException("voices cannot be null or empty");
        }
        this.voices = new ArrayList<>();
        for (Music voice : voices) {
            if (voice == null) {
                throw new IllegalArgumentException("no voice can be null");
            }
            this.voices.add(voice);
        }
        checkRep();
    }

    /**
     * Check the representation invariant.
     */
    private void checkRep() {
        assert voices != null;
        assert !voices.isEmpty();
        for (Music voice : voices) {
            assert voice != null;
        }
    }

    /**
     * @return an unmodifiable view of the voices
     */
    public List<Music> getVoices() {
        return Collections.unmodifiableList(voices);
    }

    /**
     * @return the number of voices
     */
    public int getVoiceCount() {
        return voices.size();
    }

    /**
     * @return the maximum duration among all voices
     * This represents how long the parallel music should play
     */
    public int getMaxDuration() {
        int maxDuration = 0;
        for (Music voice : voices) {
            maxDuration = Math.max(maxDuration, voice.getDuration());
        }
        return maxDuration;
    }

    /**
     * @return the minimum duration among all voices
     * This represents the shortest voice duration
     */
    public int getMinDuration() {
        int minDuration = Integer.MAX_VALUE;
        for (Music voice : voices) {
            minDuration = Math.min(minDuration, voice.getDuration());
        }
        return minDuration;
    }

    /**
     * Play all voices simultaneously starting at the same time.
     *
     * @param player  player to play on
     * @param atTicks when to start playing
     */
    public void play(SequencePlayer player, int atTicks) {
        for (Music voice : voices) {
            voice.play(player, atTicks);
        }
    }

    /**
     * Get a specific voice by index.
     * 
     * @param voiceIndex index of the voice (0-based)
     * @return the Music object at the specified index
     * @throws IndexOutOfBoundsException if voiceIndex is invalid
     */
    public Music getVoice(int voiceIndex) {
        if (voiceIndex < 0 || voiceIndex >= voices.size()) {
            throw new IndexOutOfBoundsException("voiceIndex " + voiceIndex + " is out of bounds");
        }
        return voices.get(voiceIndex);
    }

    /**
     * Create a new Parallel with an additional voice.
     * 
     * @param newVoice the voice to add
     * @return a new Parallel object with the additional voice
     */
    public Parallel addVoice(Music newVoice) {
        if (newVoice == null) {
            throw new IllegalArgumentException("newVoice cannot be null");
        }
        List<Music> newVoices = new ArrayList<>(voices);
        newVoices.add(newVoice);
        return new Parallel(newVoices);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Parallel[");
        for (int i = 0; i < voices.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("V").append(i + 1).append(": ").append(voices.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}