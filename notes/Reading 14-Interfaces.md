# Reading 14-Interfaces

## Interfaces

One way to define an abstract data type in Java is as an interface, with its implementation as a class implementing that interface.

Advantages:

- The interface specifies the contract for the client and nothing more. The interface is all a client programmer needs to read to understand the ADT.
- Multiple different representations of the abstract data type can co-exist in the same program, as different classed implementing the interface.

## Subtypes

“B is a subtype of A” means “every B is an A.” In terms of specifications: “every B satisfies the specification for A.”

`B` can only be a subtype of `A` if `B`’s specification is at least as strong as `A`’s.

 When a class implements an interface, the Java compiler ensures that every method in the interface is present in the class, with a compatible type signature. However, The compiler cannot check for all possible specification weaknesses (e.g., stricter preconditions, weaker postconditions, or looser guarantees).

> **Programmer Responsibility:**
>  When implementing a subtype (such as by implementing an interface), **you must ensure that the subtype’s specification is at least as strong as the supertype’s** to preserve correct subtyping.

## Example: `MyString`

Let’s revisit [`MyString `](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/12-abstract-data-types/#example_different_representations_for_strings). Using an interface instead of a class for the ADT, we can support multiple implementations:

```java
/** MyString represents an immutable sequence of characters. */
public interface MyString { 

    // We'll skip this creator operation for now
    // /** @param b a boolean value
    //  *  @return string representation of b, either "true" or "false" */
    // public static MyString valueOf(boolean b) { ... }

    /** @return number of characters in this string */
    public int length();

    /** @param i character position (requires 0 <= i < string length)
     *  @return character at position i */
    public char charAt(int i);

    /** Get the substring between start (inclusive) and end (exclusive).
     *  @param start starting index
     *  @param end ending index.  Requires 0 <= start <= end <= string length.
     *  @return string consisting of charAt(start)...charAt(end-1) */
    public MyString substring(int start, int end);
}
```

We’ll skip the static `valueOf `method and come back to it in a minute. Instead, let’s go ahead using a different technique from our [toolbox of ADT concepts in Java ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/12-abstract-data-types/#realizing_adt_concepts_in_java): constructors.

Here’s our first implementation:

```java
public class SimpleMyString implements MyString {

    private char[] a;

    /* Create an uninitialized SimpleMyString. */
    private SimpleMyString() {}

    /** Create a string representation of b, either "true" or "false".
     *  @param b a boolean value */
    public SimpleMyString(boolean b) {
        a = b ? new char[] { 't', 'r', 'u', 'e' } 
              : new char[] { 'f', 'a', 'l', 's', 'e' };
    }

    @Override public int length() { return a.length; }

    @Override public char charAt(int i) { return a[i]; }

    @Override public MyString substring(int start, int end) {
        SimpleMyString that = new SimpleMyString();
        that.a = new char[end - start];
        System.arraycopy(this.a, start, that.a, 0, end - start);
        return that;
    }
}
```

And here’s the optimized implementation:

```java
public class FastMyString implements MyString {

    private char[] a;
    private int start;
    private int end;

    /* Create an uninitialized FastMyString. */
    private FastMyString() {}

    /** Create a string representation of b, either "true" or "false".
     *  @param b a boolean value */
    public FastMyString(boolean b) {
        a = b ? new char[] { 't', 'r', 'u', 'e' } 
              : new char[] { 'f', 'a', 'l', 's', 'e' };
        start = 0;
        end = a.length;
    }

    @Override public int length() { return end - start; }

    @Override public char charAt(int i) { return a[start + i]; }

    @Override public MyString substring(int start, int end) {
        FastMyString that = new FastMyString();
        that.a = this.a;
        that.start = this.start + start;
        that.end = this.start + end;
        return that;
    }
}
```

- Compare these classes to the [implementations of `MyString `in *Abstract Data Types* ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/12-abstract-data-types/#example_different_representations_for_strings). Notice how the code that previously appeared in static `valueOf `methods now appears in the constructors, slightly changed to refer to the rep of `this `.

- Also notice the use of [`@Override `](https://docs.oracle.com/javase/tutorial/java/annotations/predefined.html). This annotation informs the compiler that the method must have the same signature as one of the methods in the interface we’re implementing. But since the compiler already checks that we’ve implemented all of the interface methods, the primary value of `@Override `here is for readers of the code: it tells us to look for the spec of that method in the interface. Repeating the spec wouldn’t be DRY, but saying nothing at all makes the code harder to understand.

- And notice the private empty constructors we use to make new instances in `substring(..) `before we fill in their reps with data. We didn’t have to write these empty constructors before because Java provides them by default when we don’t declare any others. Adding the constructors that take `boolean b `means we have to declare the empty constructors explicitly.

  Now that we know good ADTs scrupulously [preserve their own invariants ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/13-abstraction-functions-rep-invariants/#invariants), these do-nothing constructors are a **bad** pattern: they don’t assign any values to the rep, and they certainly don’t establish any invariants. We should strongly consider revising the implementation. Since `MyString `is immutable, a starting point would be making all the fields `final `.

How will clients use this ADT? Here’s an example:

```java
MyString s = new FastMyString(true);
System.out.println("The first character is: " + s.charAt(0));
```

This code looks very similar to the code we write to use the Java collections classes:

```java
List<String> s = new ArrayList<String>();
...
```

Unfortunately, this pattern **breaks the abstraction barrier** we’ve worked so hard to build between the abstract type and its concrete representations. Clients must know the name of the concrete representation class. Because interfaces in Java cannot contain constructors, they must directly call one of the concrete class’ constructors. The spec of that constructor won’t appear anywhere in the interface, so there’s no static guarantee that different implementations will even provide the same constructors.

Fortunately, (as of Java 8) interfaces *are* allowed to contain static methods, so we can implement the creator operation `valueOf `as a static factory method in the interface `MyString `:

```java
public interface MyString { 

    /** @param b a boolean value
     *  @return string representation of b, either "true" or "false" */
    public static MyString valueOf(boolean b) {
        return new FastMyString(true);
    }

    // ...
```

Now a client can use the ADT without breaking the abstraction barrier:

```java
MyString s = MyString.valueOf(true);
System.out.println("The first character is: " + s.charAt(0));
```

> - Defining an ADT with an interface allows for multiple implementations and demonstrates the object-oriented concept of **"polymorphism."**
> - Encapsulating object creation (**factory methods**) within **static methods of the interface** helps maintain abstraction and consistency.
> - You should avoid exposing concrete implementation classes and encourage users to program to the interface as much as possible.
> - When implementing immutable objects, **make sure that constructors always initialize all fields**, to avoid unsafe, uninitialized objects.

## Example: `Set`

Let’s consider as an example one of the ADTs from the Java collections library, `Set `. `Set `is the ADT of finite sets of elements of some other type `E `. Here is a simplified version of the `Set `interface:

```java
/** A mutable set.
 *  @param <E> type of elements in the set */
public interface Set<E> {
```

`Set `is an example of a *generic type* : a type whose specification is in terms of a placeholder type to be filled in later. Instead of writing separate specifications and implementations for `Set<String> `, `Set<Integer> `, and so on, we design and implement one `Set<E> `.

We can match Java interfaces with our classification of ADT operations, starting with a creator:

```java
    // example creator operation
    /** Make an empty set.
     *  @param <E> type of elements in the set
     *  @return a new set instance, initially empty */
    public static <E> Set<E> make() { ... } 
```

The `make `operation is implemented as a static factory method. Clients will write code like:
`Set<String> strings = Set.make();`
and the compiler will understand that the new `Set `is a set of `String `objects.

> The `make` method is **static**, meaning it belongs to the interface itself, not to any particular instance.
>
> The generic type parameter `<E>` in `public interface Set<E>` is only available to instance members and methods—not to static methods.
>
> Therefore, to make `make()` a **generic static method**, you must **declare its own type parameter**:
>  `public static <E> Set<E> make() { ... }` 



```java
    // example observer operations

    /** Get size of the set.
     *  @return the number of elements in this set */
    public int size();

    /** Test for membership.
     *  @param e an element
     *  @return true iff this set contains e */
    public boolean contains(E e);
```

Next we have two observer methods. Notice how the specs are in terms of our abstract notion of a set; it would be malformed to mention the details of any particular implementation of sets with particular private fields. These specs should apply to any valid implementation of the set ADT.

```java
    // example mutator operations

    /** Modifies this set by adding e to the set.
     *  @param e element to add */
    public void add(E e);

    /** Modifies this set by removing e, if found.
     *  If e is not found in the set, has no effect.
     *  @param e element to remove */
    public void remove(E e);
```

## Why Interfaces?

Interfaces are used pervasively in real Java code. Not every class is associated with an interface, but there are a few good reasons to bring an interface into the picture.

- **Documentation for both the compiler and for humans** . Not only does an interface help the compiler catch ADT implementation bugs, but it is also much more useful for a human to read than the code for a concrete implementation. Such an implementation intersperses ADT-level types and specs with implementation details.
- **Allowing performance trade-offs** . Different implementations of the ADT can provide methods with very different performance characteristics. Different applications may work better with different choices, but we would like to code these applications in a way that is representation-independent. From a correctness standpoint, it should be possible to drop in any new implementation of a key ADT with simple, localized code changes.
- **Optional methods** . `List `from the Java standard library marks all mutator methods as optional. By building an implementation that does not support these methods, we can provide immutable lists. Some operations are hard to implement with good enough performance on immutable lists, so we want mutable implementations, too. Code that doesn’t call mutators can be written to work automatically with either kind of list.
- **Methods with intentionally underdetermined specifications** . An ADT for finite sets could leave unspecified the element order one gets when converting to a list. Some implementations might use slower method implementations that manage to keep the set representation in some sorted order, allowing quick conversion to a sorted list. Other implementations might make many methods faster by not bothering to support conversion to sorted lists.
- **Multiple views of one class** . A Java class may implement multiple methods. For instance, a user interface widget displaying a drop-down list is natural to view as both a widget and a list. The class for this widget could implement both interfaces. In other words, we don’t implement an ADT multiple times just because we are choosing different data structures; we may make multiple implementations because many different sorts of objects may also be seen as special cases of the ADT, among other useful perspectives.
- **More and less trustworthy implementations** . Another reason to implement an interface multiple times might be that it is easy to build a simple implementation that you believe is correct, while you can work harder to build a fancier version that is more likely to contain bugs. You can choose implementations for applications based on how bad it would be to get bitten by a bug.

## Realizing ADT Concepts in Java

| ADT Concept        | Ways to Do It in Java   | Examples                                         |
| ------------------ | ----------------------- | ------------------------------------------------ |
| Abstract data type | Single class            | `String`                                         |
|                    | Interface + class(es)   | `List` and `ArrayList`                           |
| Creator operation  | Constructor             | `ArrayList()`                                    |
|                    | Static (factory) method | `Collections.singletonList()`, `Arrays.asList()` |
| Constant           | Static final field      | `BigInteger.ZERO`                                |
| Observer operation | Instance method         | `List.get()`                                     |
|                    | Static method           | `Collections.max()`                              |
| Producer operation | Instance method         | `String.trim()`                                  |
|                    | Static method           | `Collections.unmodifiableList()`                 |
| Mutator operation  | Instance method         | `List.add()`                                     |
|                    | Static method           | `Collections.copy()`                             |
| Representation     | private fields          |                                                  |

## Summary

Java interfaces help us formalize the idea of an abstract data type as a set of operations that must be supported by a type.

This helps make our code…

- **Safe from bugs.** An ADT is defined by its operations, and interfaces do just that. When clients use an interface type, static checking ensures that they only use methods defined by the interface. If the implementation class exposes other methods — or worse, has visible representation — the client can’t accidentally see or depend on them. When we have multiple implementations of a data type, interfaces provide static checking of the method signatures.
- **Easy to understand.** Clients and maintainers know exactly where to look for the specification of the ADT. Since the interface doesn’t contain instance fields or implementations of instance methods, it’s easier to keep details of the implementation out of the specifications.
- **Ready for change.** We can easily add new implementations of a type by adding classes that implement interface. If we avoid constructors in favor of static factory methods, clients will only see the interface. That means we can switch which implementation class clients are using without changing their code at all.