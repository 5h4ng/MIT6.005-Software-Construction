# Reading 18-Parser Generators

## Parser Generators

A **parser generator** takes a grammar as input and automatically generates source code that can parse streams of characters using the grammar. The generated code is a *parser*：

**Input**: A sequence of characters (like source code or an expression).

**Process**: Matches the character sequence against the grammar rules.

**Output**: Builds a **parse tree** that shows how the input matches the grammar.

The root of the parse tree is the starting nonterminal of the grammar. Each node of the parse tree expands into one production of the grammar. 

The final step of parsing is to do something useful with this parse tree. We’re going to translate it into a value of a recursive data type. Recursive abstract data types are often used to represent an expression in a language, like HTML, or Markdown, or Java, or algebraic expressions. A recursive abstract data type that represents a language expression is called an *abstract syntax tree* (AST).

## An Antler Grammar

Here is what our HTML grammar looks like as an Antlr source file:

```python
grammar Html;

root : html EOF;
html : ( italic | normal ) *;
italic : '<i>' html '</i>';
normal : TEXT; 
TEXT : ~[<>]+;  /* represents a string of one or more characters that are not < or > 
```

- **Rules** have the form:  `name : definition ;`
- **Nonterminals** (can expand further) are lowercase (e.g., `root`, `html`).
- **Terminals** (tokens) are either quoted strings (e.g., `'<i>'`) or all-uppercase names (e.g., `TEXT`, `EOF`).

`root` is the entry point; it matches the whole input.

`EOF` is a special terminal for end-of-input.

Operators:

- `|` for alternation (or)
- `*` for zero or more repetitions
- `+` for one or more repetitions
- `?` for optional (not shown in this grammar)

The `~` operator in `TEXT` means “not”; `~[<>]` matches any character except `<` or `>`.

## Generating the parser

#### **Grammar Source File**:

  - Save your grammar in a `.g4` file, e.g., `IntegerExpression.g4`.

  - This is the input for the ANTLR tool.

  - ```
    grammar IntegerExpression;
    import Configuration;
    
    root : sum EOF;
    sum : primitive ('+' primitive)*;
    primitive : NUMBER | '(' sum ')';
    NUMBER : [0-9]+;
    ```

#### **Running ANTLR Tool**:

  - Use the command line to navigate to the folder containing your grammar file.

  - Run:

    ```
    java -jar path/to/antlr.jar IntegerExpression.g4
    ```

  - Make sure the paths to both the grammar file and `antlr.jar` are correct.

#### **Generated Files**:

  - **Lexer** (`IntegerExpressionLexer.java`):
     Converts character streams to a stream of tokens (like numbers and operators).
  - **Parser** (`IntegerExpressionParser.java`):
     Consumes the token stream and builds a parse tree.
  - **Tree Walker** (`IntegerExpressionListener.java`, `IntegerExpressionBaseListener.java`):
     Lets you write code to walk and process the parse tree.
  - **Token Files**:
     Lists all tokens found in your grammar. Not always needed, but useful for grammar inclusion.

#### **Important Practices**:

  - **Never manually edit** generated Java files. Always make changes in the `.g4` grammar and regenerate.
  - **Regenerate** every time you change the grammar file.
     IDEs like Eclipse do **not** do this automatically.
  - **Refresh your IDE/project** after regeneration (e.g., press F5 in Eclipse) to ensure the latest files are used.

## Calling the parser

Now that you’ve generated the Java classes for your parser, you’ll want to use them from your own code.

First we need to make a stream of characters to feed to the lexer. Antlr has a class `ANTLRInputStream `that makes this easy. It can take a `String `, or a `Reader `, or an `InputStream `as input. Here we are using a string:

```java
CharStream stream = new ANTLRInputStream("54+(2+89)");
```

Next, we create an instance of the lexer class that our grammar file generated, and pass it the character stream:

```java
IntegerExpressionLexer lexer = new IntegerExpressionLexer(stream);
TokenStream tokens = new CommonTokenStream(lexer);
```

The result is a stream of terminals, which we can then feed to the parser:

```java
IntegerExpressionParser parser = new IntegerExpressionParser(tokens);
```

To actually do the parsing, we call a particular nonterminal on the parser. The generated parser has one method for every nonterminal in our grammar, including `root() `, `sum() `, and `primitive() `. We want to call the nonterminal that represents the set of strings that we want to match – in this case, `root() `.

<img src="assets/intexpr-parse-tree.png" alt="the parse tree produced by parsing the expression" style="zoom:50%;" />

Calling it produces a parse tree:

```java
ParseTree tree = parser.root();
```

For debugging, we can then print this tree out:

```java
System.err.println(tree.toStringTree(parser));
```

Or we can display it in a handy graphical form:

```java
Trees.inspect(tree, parser);
```

## Traversing the parse tress

Now we need to do something with this parse tree. We’re going to translate it into a value of a recursive abstract data type.

To traverse the parse tree, we use `ParseTreeWalker`, which is an Antler class that walks over a parse tree, visiting every node in order, top-to-bottom, left-to-right. 

As it visits each node in the tree, the walker calls methods on a *listener* object that we provide, which implements `IntegerExpressionListener `interface.

here’s a simple implementation of `IntegerExpressionListener `that just prints a message every time the walker calls us, so we can see how it gets used:

[`Main.java `line 88](https://github.com/mit6005/sp16-ex18-parser-generators/blob/master/src/intexpr/Main.java#L88-L120)

```java
class PrintEverything implements IntegerExpressionListener {

    @Override public void enterRoot(IntegerExpressionParser.RootContext context) {
        System.err.println("entering root");
    }
    @Override public void exitRoot(IntegerExpressionParser.RootContext context) {
        System.err.println("exiting root");
    }

    @Override public void enterSum(IntegerExpressionParser.SumContext context) {
        System.err.println("entering sum");
    }
    @Override public void exitSum(IntegerExpressionParser.SumContext context) {
        System.err.println("exiting sum");
    }

    @Override public void enterPrimitive(IntegerExpressionParser.PrimitiveContext context) {
        System.err.println("entering primitive");
    }
    @Override public void exitPrimitive(IntegerExpressionParser.PrimitiveContext context) {
        System.err.println("exiting primitive");
    }

    @Override public void visitTerminal(TerminalNode terminal) {
        System.err.println("terminal " + terminal.getText());            
    }

    // don't need these here, so just make them empty implementations
    @Override public void enterEveryRule(ParserRuleContext context) { }
    @Override public void exitEveryRule(ParserRuleContext context) { }
    @Override public void visitErrorNode(ErrorNode node) { }         
}
```

#### Listener Methods

- For every nonterminal rule 
   `N` in your grammar, the generated listener interface provides:

  - `enterN()` — called when the walker enters a parse tree node for `N`.
  - `exitN()` — called when the walker exits a parse tree node for `N`.

- For terminals (tokens), the method `visitTerminal()` is called when a leaf node is reached.

- Each listener method receives a context object (for nonterminals) or the terminal node, containing information about the visited node.

#### Additional Listener Methods

- `enterEveryRule()` and `exitEveryRule()` are called when entering or exiting any rule, useful for generic processing.
- `visitErrorNode()` is called if the parse tree contains an error node (usually not used if syntax errors throw exceptions).
- These extra methods must be implemented but can be left empty if not needed.

#### Example Output

When walking a parse tree for the input `54+(2+89)`, the output might look like:

```
entering root
entering sum
entering primitive
terminal 54
exiting primitive
terminal +
entering primitive
terminal (
entering sum
entering primitive
terminal 2
exiting primitive
terminal +
entering primitive
terminal 89
exiting primitive
exiting sum
terminal )
exiting primitive
exiting sum
terminal <EOF>
exiting root
```

This reflects a **parent-to-children, left-to-right** traversal order.

## Constructing an abstract syntax tree

##### 1. Recursive Data Type for Integer Expressions

To represent integer arithmetic expressions, we define a recursive data type:

```
IntegerExpression = Number(n: int)
                 + Plus(left: IntegerExpression, right: IntegerExpression)
```

- `Number` represents an integer value.
- `Plus` represents an addition operation between two integer expressions.

This data type is used to build an **abstract syntax tree** (AST), which captures the essential structure and meaning of the expression, without unnecessary syntactic details like parentheses or leading zeroes.

**Contrast with the parse tree (concrete syntax tree):**

- The parse tree retains all concrete syntax, including all parentheses and every token.
- The AST only preserves the semantic structure (e.g., grouping and values).

Example:
 Expressions `2+2`, `((2)+(2))`, and `0002+0002` all result in the same AST:
 `Plus(Number(2), Number(2))`

------

#### 2. Listener for Building the AST

We use an ANTLR **listener** to walk the parse tree and construct the AST. Each rule in the grammar corresponds to a method in the listener:

- When exiting a `sum` node, create a `Plus` node for the AST.
- When exiting a `primitive` node that matches a number, create a `Number` node.
- For parenthesized subexpressions, do nothing special; the grouping is already represented by the AST structure.

##### Why use a stack?

- As the listener walks the tree (bottom-up), we use a stack to keep track of the AST nodes being built.
- When exiting a rule, the stack contains all child AST nodes for that rule’s subtree. Pop them, combine them as needed, and push the new AST node back onto the stack.

##### Code Sketch


```java
class MakeIntegerExpression implements IntegerExpressionListener {
    private Stack<IntegerExpression> stack = new Stack<>();
    
    public IntegerExpression getExpression() {
        return stack.get(0);
    }

    @Override public void exitRoot(...) {
        // Do nothing; the full AST is already on the stack.
    }

    @Override public void exitSum(IntegerExpressionParser.SumContext context) {
        List<...> addends = context.primitive();
        IntegerExpression sum = stack.pop();
        for (int i = 1; i < addends.size(); ++i) {
            sum = new Plus(stack.pop(), sum);
        }
        stack.push(sum);
    }

    @Override public void exitPrimitive(IntegerExpressionParser.PrimitiveContext context) {
        if (context.NUMBER() != null) {
            int n = Integer.valueOf(context.NUMBER().getText());
            stack.push(new Number(n));
        }
        // Else, parenthesized subexpression; do nothing.
    }

    // Other interface methods can remain empty.
}
```

- On exiting each node, combine children from the stack to build the parent node.
- After the whole tree is walked, the stack contains the final AST.

------

#### 3. Error Handling in ANTLR

- By default, ANTLR prints parsing errors to the console.

- For modular error handling, attach an `ErrorListener` to the lexer and parser to throw exceptions on error, instead of printing.

- For example:

  ```
  lexer.reportErrorsAsExceptions();
  parser.reportErrorsAsExceptions();
  ```

- This approach is simple. For more advanced error recovery, refer to the ANTLR documentation.

## Summary

The topics of today’s reading connect to our three properties of good software as follows:

- **Safe from bugs.** A grammar is a declarative specification for strings and streams, which can be implemented automatically by a parser generator. These specifications are often simpler, more direct, and less likely to be buggy then parsing code written by hand.
- **Easy to understand.** A grammar captures the shape of a sequence in a form that is compact and easier to understand than hand-written parsing code.
- **Ready for change.** A grammar can be easily edited, then run through a parser generator to regenerate the parsing code.

