# Reading 15-Equality

## Three Ways to Regard Equality

**Using an abstraction function** . Recall that an abstraction function f: R → A maps concrete instances of a data type to their corresponding abstract values. To use f as a definition for equality, we would say that a equals b if and only if f(a)=f(b).

**Using a relation** . An *equivalence* is a relation E ⊆ T x T that is:

- reflexive: E(t,t) ∀ t ∈ T
- symmetric: E(t,u) ⇒ E(u,t)
- transitive: E(t,u) ∧ E(u,v) ⇒ E(t,v)

To use E as a definition for equality, we would say that a equals b if and only if E(a,b).

These two notions are equivalent. An equivalence relation induces an abstraction function (the relation partitions T, so f maps each element to its partition class). The relation induced by an abstraction function is an equivalence relation (check for yourself that the three properties hold).

A third way we can talk about the equality between abstract values is in terms of what an outsider (a client) can observe about them:

**Using observation** . We can say that two objects are equal when they cannot be distinguished by observation – every operation we can apply produces the same result for both objects. Consider the set expressions {1,2} and {2,1}. Using the observer operations available for sets, cardinality |…| and membership ∈, these expressions are indistinguishable:

- |{1,2}| = 2 and |{2,1}| = 2
- 1 ∈ {1,2} is true, and 1 ∈ {2,1} is true
- 2 ∈ {1,2} is true, and 2 ∈ {2,1} is true
- 3 ∈ {1,2} is false, and 3 ∈ {2,1} is false
- … and so on

In terms of abstract data types, “observation” means calling operations on the objects. So two objects are equal if and only if they cannot be distinguished by calling any operations of the abstract data type.

## == vs. equals()

Like many languages, Java has two different operations for testing equality, with different semantics.

- The `== `operator compares **references**. More precisely, it tests *referential* equality. Two references are == if they point to the same storage in memory. In terms of the snapshot diagrams we’ve been drawing, two references are `== `if their arrows point to the same object bubble.
- The `equals() `operation compares **object contents** – in other words, *object* equality, in the sense that we’ve been talking about in this reading. The equals operation has to be defined appropriately for every abstract data type.

For comparison, here are the equality operators in several languages:

|             | *referential equality * | *object equality * |
| ----------- | ----------------------- | ------------------ |
| Java        | `==`                    | `equals()`         |
| Objective C | `==`                    | `isEqual:`         |
| C#          | `==`                    | `Equals()`         |
| Python      | `is`                    | `==`               |
| Javascript  | `==`                    | n/a                |

## Equality of Immutable Types

The `equals() `method is defined by `Object `, and its default implementation looks like this:

```java
public class Object {
    
    public boolean equals(Object that) {
        return this == that;
    }
}
```

In other words, the default meaning of `equals() `is the same as referential equality. For immutable data types, this is almost always wrong. So you have to **override** the `equals() `method, replacing it with your own implementation.

Here’s our first try for `Duration `:

```java
public class Duration {
    private final int mins;
    private final int secs;
    // rep invariant:
    //    mins >= 0, secs >= 0
    // abstraction function:
    //    represents a span of time of mins minutes and secs seconds

    /** Make a duration lasting for m minutes and s seconds. */
    public Duration(int m, int s) {
        mins = m; secs = s;
    }
    /** @return length of this duration in seconds */
    public long getLength() {
        return mins*60 + secs;
    }
    // Problematic definition of equals()
    public boolean equals(Duration that) {
        return this.getLength() == that.getLength();        
    }
}
```