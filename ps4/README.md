# Problem Set 4: Multiplayer Minesweeper

## Overview

### Restrictions
Do not change `main()` or `runMinesweeperServer()` (signatures or behavior).

**Board coordinates:**
- Origin `(0,0)` = top-left.
- X increases right, Y increases down.

### Game Rules (Multiplayer)

- Each square has three states:
  - `untouched`
  - `dug`
  - `flagged`
- Contains either a bomb or no bomb
- When digging a bomb
  - That player loses, and server closes their connection.
  - Square becomes dug & safe for the rest, and game continues.
- Players may reconnect at any time.
- No win condition required.

### Concurrency Challenges
- Multiple players share **one board**.
- Must ensure **thread-safe board operations** (locks, synchronization)
- Allow different players to see different board states in real time

### Telnet client

`telnet` is a utility that allows you to make a direct network connection to a listening server and communicate with it 
via a terminal interface.

Connect to a server:
```bash
telnet localhost 4444
```

Commands:
- `dig x y` → uncover square
- `flag x y` → place flag
- `deflag x y` → remove flag

Response: 
- server returns updated ASCII board.

Exit:
- Auto: when server closes connection.
- Manual: `Ctrl + ]` → quit.

## Protocal and specification

### Messages from the user to the server

Formal grammar
```
MESSAGE ::= ( LOOK | DIG | FLAG | DEFLAG | HELP_REQ | BYE ) NEWLINE
LOOK ::= "look"
DIG ::= "dig" SPACE X SPACE Y
FLAG ::= "flag" SPACE X SPACE Y
DEFLAG ::= "deflag" SPACE X SPACE Y
HELP_REQ ::= "help"
BYE ::= "bye"
NEWLINE ::= "\n" | "\r" "\n"?
X ::= INT
Y ::= INT
SPACE ::= " "
INT ::= "-"? [0-9]+
```

The NEWLINE can be a single character `"\n"` or `"\r"` or the two-character sequence `"\r\n"` , the same definition used by `BufferedReader.readLine()`.

#### LOOK

Returns a BOARD message, a string representation of the board’s state. Does not mutate anything on the server. See the section below on messages from the server to the user for the exact required format of the BOARD message.

#### DIG

The message is the word “dig” followed by two arguments, the X and Y coordinates. The type and the two arguments are seperated by a single SPACE.

**Behavior**:
1. If `(x,y)` out of range OR not `untouched` → no change, return `BOARD`.
2. If `untouched` → change to `dug`.
3. If square had a bomb:
    - Remove bomb.
    - Send `BOOM` message.
    - If debug flag **not** set → terminate client connection.
    - Update neighbor bomb counts in future `BOARD`s.
4. If no neighbors with bombs → recursively dig all untouched neighbors.
- **Response**:
- `BOARD` (if no explosion), or
- `BOOM` (if bomb).  

#### FLAG

**Effect**:
- If `(x,y)` is `untouched` → change to `flagged`.
- Otherwise, no change.

**Response**: Always return `BOARD`.  

#### DEFLAG

**Effect**:
- If `(x,y)` is `flagged` → change to `untouched`.
- Otherwise, no change.

**Response**: Always return `BOARD`.  

#### HELP

- **Effect**: Return a `HELP` message.
- **State**: Does not mutate server state.  

#### BYE

- **Effect**: Terminate connection with this client.
- **Response**: None.  

### Message from the server to the user

Formal grammar
```
MESSAGE ::= BOARD | BOOM | HELP | HELLO
BOARD ::= LINE+
LINE ::= (SQUARE SPACE)* SQUARE NEWLINE
SQUARE ::= "-" | "F" | COUNT | SPACE
SPACE ::= " "
NEWLINE ::= "\n" | "\r" "\n"?
COUNT ::= [1-8]
BOOM ::= "BOOM!" NEWLINE
HELP ::= [^\r\n]+ NEWLINE
HELLO ::= "Welcome to Minesweeper. Board: " X " columns by " Y " rows. Players: " N
" including you. Type 'help' for help." NEWLINE
X ::= INT
Y ::= INT
N ::= INT
INT ::= "-"? [0-9]+
```

The server sends the HELLO message as soon as it establishes a connection to the user. After that, 
for any message it receives that matches the user-to-server message format, other than a BYE message, the server should always return either a BOARD message, a BOOM message, or a HELP message.

#### HELLO Message
**Format**: Sent exactly **once**, immediately after the server connects to a user.

**Contents**:
- `N`: current number of users connected (including this one).
- Board dimensions: `X × Y`.
- Ends with a `NEWLINE`. 

#### BOARD Message

- No start keyword.
- Series of newline-separated rows, each row being space-separated characters.
- Exactly one character per square.

**Character mapping**:
- `-` → untouched square
- `F` → flagged square
- `" "` (space) → dug square with **0** neighboring bombs
- `[1-8]` → dug square with that many neighboring bombs  

- **Coordinate system**:
- `(0,0)` is the **top-left** cell.
- X increases rightwards, Y increases downwards.
- This differs from standard math quadrants but is per spec.  

#### BOOM

- **Effect**: Sent when the client digs a bomb.
- **Connection**: This is the **last message** the client receives before being disconnected.

#### HELP

## Problem 1: set up the server to deal with multiple clients

https://docs.oracle.com/javase/tutorial/networking/sockets/index.html