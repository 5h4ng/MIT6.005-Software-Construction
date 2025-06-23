# Reading 10-Recursion

## Recursion

A recursive function is defined in terms of **base cases** and **recursive steps**.

- **In a base case**, we compute the result immediately given the inputs to the function call.
- **In a recursive step**, we compute the result with the help of one or more **recursive calls** to this same function, but with the inputs somehow reduced in size and complexity, closer to a base case;

## Choosing the Right Decomposition for a Problem

For example, `subsequences("abc") `might return `"abc,ab,bc,ac,a,b,c," `. Note the trailing comma preceding the empty subsequence, which is also a valid subsequence.

```java
/**
 * @param word consisting only of letters A-Z or a-z
 * @return all subsequences of word, separated by commas,
 * where a subsequence is a string of letters found in word 
 * in the same order that they appear in word.
 */
public static String subsequences(String word) {
     if (word.isEmpty()) {
         return ""; // base case
     } else {
         char firstLetter = word.charAt(0); // get the first character of the string.
         String restOfWord = word.substring(1); // get the rest of the string after the first character
         
         String subsequencesOfRest = subsequences(restOfWord);         
        String result = "";
        for (String subsequence : subsequencesOfRest.split(",", -1))  //With -1, you get all parts, including the empty string at the end.
             result += "," + subsequence;
             result += "," + firstLetter + subsequence;
         }
         result = result.substring(1); // remove extra leading comma
        return result;
     }
}
```

## Helper Methods

- Use private helper methods with extra parameters to simplify and clarify your recursive code.

- Keep helper methods hidden; only the public method should be exposed to users.

- Don’t change your method’s public specification for recursion—handle extra parameters internally.

- Choose recursive decompositions that make your code as simple and natural as possible.

```java
/**
 * Return all subsequences of word (as defined above) separated by commas,
 * with partialSubsequence prepended to each one.
 */
private static String subsequencesAfter(String partialSubsequence, String word) {
    if (word.isEmpty()) {
        // base case
        return partialSubsequence;
    } else {
        // recursive step
        return subsequencesAfter(partialSubsequence, word.substring(1))
             + ","
             + subsequencesAfter(partialSubsequence + word.charAt(0), word.substring(1));
    }
}

public static String subsequences(String word) {
    return subsequencesAfter("", word);
}
```

## Recursive Problems vs. Recursive Data

**filesystem** is a recursive data, which consists of named **files**. Some files are **folders**, which can contain other files. 

The Java library represents the file system using [`java.io.File `](https://docs.oracle.com/javase/8/docs/api/index.html?java/io/File.html). This is a recursive data type, in the sense that `f.getParentFile() `returns the parent folder of a file `f `, which is a `File `object as well, and `f.listFiles() `returns the files contained by `f `, which is an array of other `File `objects.

For recursive data, it’s natural to write recursive implementations:

```java
/**
 * @param f a file in the filesystem
 * @return the full pathname of f from the root of the filesystem
 */
public static String fullPathname(File f) {
    if (f.getParentFile() == null) {
        // base case: f is at the root of the filesystem
        return f.getName();  
    } else {
        // recursive step
        return fullPathname(f.getParentFile()) + "/" + f.getName();
    }
}
```

Recent versions of Java have added a new API, [`java.nio.Files `](https://docs.oracle.com/javase/8/docs/api/index.html?java/nio/file/Files.html)and [`java.nio.Path `](https://docs.oracle.com/javase/8/docs/api/index.html?java/nio/file/Path.html), which offer a cleaner separation between the filesystem and the pathnames used to name files in it. But the data structure is still fundamentally recursive.

## Reentrant Codes

- **Recursion**: a method calling itself, which is a special case of **reentrancy**
- **Reentrancy（可重入性）**：Reentrant code can be safely re-entered, meaning that it can be called again *even while a call to it is underway*

Reentrant code keeps its state **entirely in parameters** and **local variables**, and **doesn’t use static variables or global variables**, and **doesn’t share aliases to mutable objects** with other parts of the program, or other calls to itself.

When we talk about **concurrency** later in the course, reentrancy will come up again, since in a concurrent program, a method may be called at the same time by different parts of the program that are running concurrently.

It’s good to design your code to be reentrant as much as possible. Reentrant code is safer from bugs and can be used in more situations, like **concurrency, callbacks, or mutual recursion.**

## When to Use Recursion Rather Than Iteration

- The problem is naturally recursive (e.g. Fibonacci)
- The data is naturally recursive (e.g. filesystem)

Another reason to use recursion is to **take more advantage of immutability**. In an ideal recursive implementation, all variables are final, all data is immutable, and the recursive methods **are all pure functions** in the sense that they do not mutate anything. The behavior of a method can be understood simply as a relationship between its parameters and its return value, with no side effects on any other part of the program. This kind of paradigm is called ***functional programming*** , and it is far easier to reason about than ***imperative programming*** with loops and variables. 

In **iterative implementations**, by contrast, you inevitably have **non-final variables or mutable objects** that are modified during the course of the iteration. Reasoning about the program then requires thinking about snapshots of the program state at various points in time, rather than thinking about pure input/output behavior.

One downside of recursion is that it may **take more space than an iterative solution**. Building up a stack of recursive calls consumes memory temporarily, and **the stack is limited in size**, which may become a limit on the size of the problem that your recursive implementation can solve.

## Common Mistakes in Recursive Implementations

- **The base case is missing entirely**, or the problem needs more than one base case but not all the base cases are covered.
- The recursive step **doesn’t reduce to a smaller subproblem**, so the recursion doesn’t converge.

On the bright side, what would be an infinite loop in an iterative implementation usually becomes a `StackOverflowError `in a recursive implementation. A buggy recursive program fails faster.

## Summary

We saw these ideas:

- recursive problems and recursive data
- comparing alternative decompositions of a recursive problem
- using helper methods to strengthen a recursive step
- recursion vs. iteration

The topics of today’s reading connect to our three key properties of good software as follows:

- **Safe from bugs.** Recursive code is simpler and often uses immutable variables and immutable objects.
- **Easy to understand.** Recursive implementations for naturally recursive problems and recursive data are often shorter and easier to understand than iterative solutions.
- **Ready for change.** Recursive code is also naturally reentrant, which makes it safer from bugs and ready to use in more situations.