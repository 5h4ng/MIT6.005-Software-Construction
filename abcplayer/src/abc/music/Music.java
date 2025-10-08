package abc.music;

import abc.sound.SequencePlayer;

/**
 * Music represents a piece of music.
 */
public interface Music {

    /**
     * @return total duration of this piece in ticks
     */
    int getDuration();

    /**
     * Play this piece.
     * @param player player to play on
     * @param atTicks when to play
     */
    void play(SequencePlayer player, int atTicks);
}
