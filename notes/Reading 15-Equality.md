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

The `equals() `method is defined by `Object`, and its default implementation looks like this:

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

There’s a subtle problem here. Why doesn’t this work? Let’s try this code:

```java
Duration d1 = new Duration (1, 2);
Duration d2 = new Duration (1, 2);
Object o2 = d2;
d1.equals(d2) → true
d1.equals(o2) → false
```

It turns out that `Duration `has **overloaded** the `equals() `method, because the method signature was not identical to `Object `’s. We actually have two `equals() `methods in `Duration `: an implicit `equals(Object) `inherited from `Object `, and the new `equals(Duration) `.

```java
public class Duration extends Object {
    // explicit method that we declared:
    public boolean equals (Duration that) {
        return this.getLength() == that.getLength();
    }
    // implicit method inherited from Object:
    public boolean equals (Object that) {
        return this == that;
    }
}
```

> - **Overriding** happens when a subclass provides a new implementation for a method **with the exact same signature** (same name, same parameter types) as a method in its superclass.
> - **Overloading** happens when a class has **multiple methods** with the **same name but different parameter lists** (different types or number of parameters).

It’s easy to make a mistake in the method signature, and overload a method when you meant to override it. This is such a common error that Java has a language feature, the annotation [`@Override `](https://docs.oracle.com/javase/tutorial/java/annotations/predefined.html), which you should use whenever your intention is to override a method in your superclass. With this annotation, the Java compiler will check that a method with the same signature actually exists in the superclass, and give you a compiler error if you’ve made a mistake in the signature.

So here’s the right way to implement `Duration `’s `equals() `method:

```java
@Override
public boolean equals (Object thatObject) {
    if (!(thatObject instanceof Duration)) return false;
    Duration thatDuration = (Duration) thatObject;
    return this.getLength() == thatDuration.getLength();
}
```

> using `instanceof `is dynamic type checking, not the static type checking we vastly prefer. In general, using `instanceof `in object-oriented programming is a bad smell. In 6.005 — and this is another of our rules that holds true in most good Java programming — **`instanceof `is disallowed anywhere except for implementing `equals `**.

## The Object Contract

The specification of the `Object `class is so important that it is often referred to as *the `Object `Contract* . The contract can be found in the method specifications for the `Object `class. Here we will focus on the contract for `equals `. When you override the `equals `method, you must adhere to its general contract. It states that:

- `equals `must define an equivalence relation – that is, a relation that is reflexive, symmetric, and transitive;
- `equals `must be consistent: repeated calls to the method must yield the same result provided no information used in `equals `comparisons on the object is modified;
- for a non-null reference `x `, `x.equals(null) `should return false;
- `hashCode `must produce the same result for two objects that are deemed equal by the `equals `method.

### `hashCode `

#### 1. Why `hashCode()` Matters

- **Hash-based collections** like `HashSet` and `HashMap` use hash tables for fast (constant-time) lookup.
- Each key is stored in a "bucket" determined by its `hashCode()`.
- For correct operation, Java requires:
  - If two objects are **equal** (`equals()` returns `true`), they **must** have the **same** `hashCode()`.

#### 2. The Hash Table Mechanism

- On insertion, the hash table computes `hashCode()` of the key, then maps it to a bucket (e.g., using modulo).
- If two keys have the same hash code (collision), their key-value pairs are stored together in a list (the "bucket").
- Lookup is a two-step process:
  1. Find the right bucket using `hashCode()`
  2. Scan the bucket using `equals()` to find the correct entry.

#### 3. Consequences of Breaking the Contract

- If **equal objects** have **different hash codes**, they go to different buckets. Lookups using an equal key will **fail**.
- The default `Object.hashCode()` is based on the memory address. If you override `equals()` but **not** `hashCode()`, you almost always **break the contract**.
- A trivial but correct (yet inefficient) implementation is returning a constant value. This prevents contract violation but **kills performance**—every key is placed in the same bucket, causing lookups to degrade to linear search.

#### 4. How to Correctly Implement `hashCode()`

- Always override `hashCode()` **whenever** you override `equals()`.
- The hash code should combine the hash codes of all fields used in `equals()`.
- Use utility methods like `Objects.hash(...)` (Java 7+) for convenience.

> **Always override `hashCode `when you override `equals `.**
>
> The advice is summarized in [a good StackOverflow post ](https://stackoverflow.com/questions/113511/hash-code-implementation). Recent versions of Java now have a utility method [`Objects.hash() `](https://docs.oracle.com/javase/8/docs/api/java/util/Objects.html#hash-java.lang.Object...-)that makes it easier to implement a hash code involving multiple fields.

## Equality of Mutable Types

Recall our definition: two objects are equal when they cannot be distinguished by observation. With mutable objects, there are two ways to interpret this:

- when they cannot be distinguished by observation *that doesn’t change the state of the objects* , i.e., by calling only observer, producer, and creator methods. This is often strictly called **observational equality** , since it tests whether the two objects “look” the same, in the current state of the program.
- when they cannot be distinguished by *any* observation, even state changes. This interpretation allows calling any methods on the two objects, including mutators. This is often called **behavioral equality** , since it tests whether the two objects will “behave” the same, in this and all future states.

### Notes: Equality of Mutable Types

#### 1. **Definition of Equality**

- Two objects are considered equal if they cannot be distinguished by observation.

#### 2. **Two Interpretations for Mutable Objects**

- **Observational Equality:**
   Objects are equal if they appear the same by using only observer, producer, and creator methods (i.e., methods that do not change state).
- **Behavioral Equality:**
   Objects are equal if they cannot be distinguished by *any* observation, including after applying mutator methods (i.e., they behave the same now and in all possible future states).

#### 3. **Immutable vs Mutable Objects**

- For **immutable objects**, observational and behavioral equality are the same (no mutators).
- For **mutable objects**, the two can differ.

#### 4. **Problems with Observational Equality for Mutable Objects**

- Java collections (like `List`) use observational equality:
   Two lists with the same elements are `equals()`, even if they are different objects.

- Adding a mutable object (like a list) to a set can cause bugs if the object is mutated later:

  - The set uses the object's `hashCode()` to store and find it.
  - If the object's state changes, its `hashCode()` changes, but its position in the set does **not** update.
  - `set.contains()` can fail even though the set "contains" the object, breaking the set's invariants.

#### 5. **Java's Inconsistency**

- Some mutable classes (collections) use observational equality.
- Others (like `StringBuilder`) use behavioral equality (only equal if they are the same object).

#### 6. **Best Practice**

- For mutable objects, `equals()` should usually implement **behavioral equality**:
   Only return `true` if two references point to the exact same object (i.e., default `Object.equals()`).
- If clients need observational equality ("look the same right now"), define a separate method (e.g., `similar()`).

## The Final Rule for equals() and hashCode()

**For immutable types** :

- `equals() `should compare abstract values. This is the same as saying `equals() `should provide behavioral equality.
- `hashCode() `should map the abstract value to an integer.

So immutable types must override both `equals() `and `hashCode() `.

**For mutable types** :

- `equals() `should compare references, just like `== `. Again, this is the same as saying `equals() `should provide behavioral equality.
- `hashCode() `should map the reference into an integer.

So mutable types should not override `equals() `and `hashCode() `at all, and should simply use the default implementations provided by `Object `. Java doesn’t follow this rule for its collections, unfortunately, leading to the pitfalls that we saw above.

### Autoboxing and Equality in Java

#### 1. **Primitive Types vs. Wrapper Types**

- Primitive types (e.g., `int`)

   - Store values directly.
  - `==` compares values.
  
- Wrapper types (e.g., `Integer`):

   - Are objects.
  - `==` compares references (are they the same object?).
  - `.equals()` compares values.

#### 2. **Autoboxing and Autounboxing**

- **Autoboxing:** Automatic conversion from a primitive to its wrapper type (e.g., `int` to `Integer`).
- **Autounboxing:** Automatic conversion from a wrapper type to its primitive (e.g., `Integer` to `int`).

#### 3. **Pitfalls with Equality**

- When using wrapper types like `Integer`, `==` checks reference equality, **not value equality**.
- `.equals()` checks if the values are the same.

**Example:**

```java
Integer x = new Integer(3);
Integer y = new Integer(3);
System.out.println(x == y);       // false (different objects)
System.out.println(x.equals(y));  // true  (same value)
```

#### 4. **Collections and Autoboxing**

- When storing values in collections (like `Map<String, Integer>`), autoboxing happens automatically.
- Retrieving values from two different maps with the same key and value gives you two different `Integer` objects (unless the value is cached by Java, which only happens for values between -128 and 127).

**Example:**

```java
Map<String, Integer> a = new HashMap<>();
Map<String, Integer> b = new HashMap<>();
a.put("c", 130);
b.put("c", 130);
System.out.println(a.get("c") == b.get("c"));       // false (usually)
System.out.println(a.get("c").equals(b.get("c")));  // true
```

#### 5. **Best Practices**

- **Never use `==` to compare wrapper types for value equality.** Always use `.equals()`.
- Be careful with autoboxing and autounboxing, and always know whether you're working with primitives or objects.

## Summary

- Equality should be an equivalence relation (reflexive, symmetric, transitive).
- Equality and hash code must be consistent with each other, so that data structures that use hash tables (like `HashSet `and `HashMap `) work properly.
- The abstraction function is the basis for equality in immutable data types.
- **Reference equality** is the basis for equality in **mutable data types**; this is the only way to ensure consistency over time and avoid breaking rep invariants of hash tables.

Equality is one part of implementing an abstract data type, and we’ve already seen how important ADTs are to achieving our three primary objectives. Let’s look at equality in particular:

- **Safe from bugs** . Correct implementation of equality and hash codes is necessary for use with collection data types like sets and maps. It’s also highly desirable for writing tests. Since every object in Java inherits the `Object `implementations, immutable types must override them.
- **Easy to understand** . Clients and other programmers who read our specs will expect our types to implement an appropriate equality operation, and will be surprised and confused if we do not.
- **Ready for change** . Correctly-implemented equality for *immutable* types separates equality of reference from equality of abstract value, hiding from clients our decisions about whether values are shared. Choosing behavioral rather than observational equality for *mutable* types helps avoid unexpected aliasing bugs.