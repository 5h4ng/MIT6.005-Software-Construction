# ABC Music Player Specification

## Overview

The ABC Music Player is a Java application that parses ABC notation files and plays them using the Java MIDI API. ABC notation is a text-based music notation system that allows musicians to transcribe and share music in a simple, readable format.

## ABC Notation Format

### Header Section
The header contains metadata about the piece:

- `X:` - Reference number (unique identifier)
- `T:` - Title of the piece
- `C:` - Composer
- `M:` - Meter (time signature, e.g., 4/4, 3/4, 2/4)
- `L:` - Default note length (e.g., 1/4, 1/8)
- `Q:` - Tempo (e.g., 1/4=120 means 120 quarter notes per minute)
- `K:` - Key signature (e.g., C, G, F, Am, Dm)

### Body Section
The body contains the actual music notation:

#### Basic Notes
- `C`, `D`, `E`, `F`, `G`, `A`, `B` - Natural notes
- `^C` - Sharp (C#)
- `_C` - Flat (Cb)
- `=C` - Natural (explicitly cancels sharp/flat)

#### Octaves
- `C` - Middle C
- `c` - C one octave above middle C
- `C,` - C one octave below middle C
- `c'` - C two octaves above middle C
- `C,,` - C two octaves below middle C

#### Note Lengths
- `C` - Default length (as specified in L: field)
- `C2` - Double length
- `C/2` - Half length
- `C3/2` - Dotted note (1.5x length)
- `C/4` - Quarter length

#### Rests
- `z` - Rest of default length
- `z2` - Rest of double length
- `z/2` - Rest of half length

#### Chords
- `[CEG]` - Chord of C, E, G played simultaneously

#### Tuplets
- `(3CDE` - Triplet (3 notes in time of 2)
- `(2CD` - Duplet (2 notes in time of 3)
- `(4CDEF` - Quadruplet (4 notes in time of 3)

#### Repeats
- `|` - Bar line
- `||` - Double bar line (end of piece)
- `|:` - Start repeat
- `:|` - End repeat
- `|1` - First ending
- `|2` - Second ending

#### Voices
- `V:1` - Voice 1
- `V:2` - Voice 2
- Multiple voices can be played simultaneously

## System Architecture

### Core Components

1. **Parser Module** (`abc.parser`)
   - ANTLR grammar files for parsing ABC notation
   - Converts text input into parse trees
   - Handles header and body parsing separately

2. **Music AST Module** (`abc.music`)
   - Abstract Syntax Tree representation of music
   - Immutable data structures
   - Recursive structure for complex musical constructs

3. **Sound Module** (`abc.sound`)
   - MIDI sequence player
   - Pitch representation and manipulation
   - Audio playback functionality

4. **Player Module** (`abc.player`)
   - Main application entry point
   - Coordinates parsing, AST construction, and playback

### Data Flow

```
ABC File → Parser → Parse Tree → AST Builder → Music AST → Sequence Player → MIDI Output
```

## Music AST Design

### Interface: Music

```java
public interface Music {
    int getDuration();  // Returns duration in ticks
    void play(SequencePlayer player, int atTicks);  // Plays the music
}
```

### Variants

#### Primitive Types
1. **Note(duration, pitch)**
   - Represents a single musical note
   - Duration in ticks, pitch as MIDI note number

2. **Rest(duration)**
   - Represents silence
   - Duration in ticks

3. **Chord(duration, pitches)**
   - Multiple pitches played simultaneously
   - Duration in ticks, list of pitches

#### Composite Types
1. **Concat(first, second)**
   - Sequential composition
   - First music followed by second

2. **Parallel(voices)**
   - Simultaneous composition
   - Multiple voices played at the same time

3. **Tuplet(tupletType, notes)**
   - Modified time grouping
   - tupletType: 2 (duplet), 3 (triplet), 4 (quadruplet)

4. **Repeat(music, repeatCount)**
   - Repeats music a specified number of times
   - Handles first/second endings

## Key Design Principles

### Immutability
- All Music variants are immutable
- No mutation of existing objects
- Safe for concurrent access

### Recursive Structure
- Music can contain other Music objects
- Enables complex musical structures
- Natural representation of musical hierarchy

### Separation of Concerns
- Parser handles text-to-tree conversion
- AST represents musical structure
- Player handles audio output
- Clear interfaces between modules

### Error Handling
- Graceful handling of malformed input
- Clear error messages
- Robust parsing with recovery

## Implementation Requirements

### Parser Requirements
- Support for all specified ABC notation elements
- Proper handling of whitespace and comments
- Cross-platform line ending support
- Error recovery for malformed input

### AST Requirements
- Complete implementation of all Music variants
- Proper abstraction functions and rep invariants
- Comprehensive toString() methods
- checkRep() methods for debugging

### Player Requirements
- Accurate timing and rhythm
- Proper handling of tempo changes
- Support for multiple voices
- MIDI-compatible output

### Testing Requirements
- Unit tests for all components
- Integration tests for complete workflow
- Test cases for edge cases and error conditions
- Performance tests for large files

## Example Usage

```java
// Parse an ABC file
Music music = AbcParser.parse("sample.abc");

// Create a sequence player
SequencePlayer player = new SequencePlayer(120, 4);

// Play the music
music.play(player, 0);
player.play();
```

## File Structure

```
src/abc/
├── music/           # Music AST classes
├── parser/          # ANTLR grammar files
├── player/          # Main application
└── sound/           # MIDI and audio classes

test/abc/
└── sound/           # Test classes

sample_abc/          # Example ABC files
```

This specification provides the foundation for implementing a robust, maintainable ABC music player that follows software engineering best practices.
