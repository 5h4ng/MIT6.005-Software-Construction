# Design of ABC Music Player

## Music AST

### Interface `Music`

```
Music = Note(duration:double, pitch:Pitch, instrument:Instrument)
        + Rest(duration:double)
        + Chord(duration:double, notes:List<Pitch>, instrument:Instrument)
        + Tuplet(tupletType:int, notes:List<Music>)
        + Concat(first:Music, second:Music)
        + Parallel(voices:List<Music>)
```

- `getDuration`: return duration in ticks
- `play`: use sequencePlayer to play the piece

### Variant Descriptions

#### Note (primitive)
   Represents a single musical note.

#### Rest (primitive)
   Represents silence.

#### Chord (primitive)
   Represents multiple pitches played simultaneously.

#### Tuplet (composite)
   Represents a group of notes played in modified time (duplet/triplet/quadruplet).

```
// AF(tupletType, notes) = notes played with time modification based on tupletType
//   tupletType=2: duplet (2 notes in time of 3)
//   tupletType=3: triplet (3 notes in time of 2)  
//   tupletType=4: quadruplet (4 notes in time of 3)
// RI: tupletType in {2,3,4}, notes.size() == tupletType
```

#### Concat (composite)
   Represents sequential composition - first music followed by second.

```
// AF(first, second) = first played, then second
// RI: true
```

#### Parallel (composite)
   Represents parallel composition - multiple voices played simultaneously.

```
// AF(voices) = all voices in the list played at the same time
// RI: voices.size() >= 1
```




