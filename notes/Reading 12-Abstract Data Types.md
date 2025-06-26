# Reading 12-Abstract Data Types

## What Abstraction Means

**Terms:**

- **Abstraction**. Omitting or hiding low-level details with a simpler, higher-level idea.
- **Modularity**. Dividing a system into components or modules, each of which can be designed, implemented, tested, reasoned about, and reused separately from the rest of the system.
- **Encapsulatioin**. Building walls around a module (a hard shell or capsule) so that the module is responsible for its own internal behavior, and bugs in other parts of the system can't damage its integrity.
- **Information hiding**. Hiding details of a module's implementation from the rest of the system, so that those details can be changed later without changing the rest of the system.
- **Separation of concerns**. Making a feature (or 'concern') the responsibility of a single module, rather than spreading it across multiple modules.

## User-Defined Types

The idea of abstract types - one could design a programming language to allow user-defined types - was a major advance in software development. 

The **key ideas** of data abstraction is that a type is characterized by **the operations** you can perform on it. 

- A number is something you can add and mutiply;
- A string is something you can concatenate and take substrings of;
- A boolean is something you can negate

## Classifying Types and Operations

**Types**, whether built-in or user-defined, can be classified as **mutable** or **immutable**. 

The **operations** of an abstract type are classified as follows:

- **Creators** create new objects of the type. A creator may take an object as an argument, but not an object of the type being constructed.
- **Producers** create new objects from old objects of the type. The `concat `method of `String `, for example, is a producer: it takes two strings and produces a new one representing their concatenation.
- **Observers** take objects of the abstract type and return objects of a different type. The `size `method of `List `, for example, returns an `int `.
- **Mutators** change objects. The `add `method of `List `, for example, mutates a list by adding an element to the end.

A creator operation is often implemented as a *constructor* , like [`new ArrayList() `](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ArrayList--). But a creator can simply be a static method instead, like [`Arrays.asList() `](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#asList-T...-). A creator implemented as a static method is often called a **factory method** . The various [`String.valueOf `](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#valueOf-boolean-)methods in Java are other examples of creators implemented as factory methods.

Mutators are often signaled by a `void `return type. A method that returns void *must* be called for some kind of side-effect, since otherwise it doesn’t return anything. But not all mutators return void. For example, [`Set.add() `](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html#add-E-)returns a boolean that indicates whether the set was actually changed. In Java’s graphical user interface toolkit, [`Component.add() `](https://docs.oracle.com/javase/8/docs/api/java/awt/Container.html#add-java.awt.Component-)returns the object itself, so that multiple `add() `calls can be [chained together ](https://en.wikipedia.org/wiki/Method_chaining).

## Abstract Data Type Examples

**`int `**is Java’s primitive integer type. `int `is immutable, so it has no mutators.

- creators: the numeric literals `0 `, `1 `, `2 `, …
- producers: arithmetic operators `+ `, `- `, `* `, `/`
- observers: comparison operators `== `, `!= `, `< `, `>`
- mutators: none (it’s immutable)

**`List `**is Java’s list type. `List `is mutable. `List `is also an interface, which means that other classes provide the actual implementation of the data type. These classes include `ArrayList `and `LinkedList `.

- creators: `ArrayList `and `LinkedList `constructors, [`Collections.singletonList`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#singletonList-T-)
- producers: [`Collections.unmodifiableList`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#unmodifiableList-T-) ("wrapper")
- observers: `size `, `get`
- mutators: `add `, `remove `, `addAll `, [`Collections.sort`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#sort-java.util.List-)

**`String `**is Java’s string type. `String `is immutable.

- creators: `String `constructors
- producers: `concat `, `substring `, `toUpperCase`
- observers: `length `, `charAt`
- mutators: none (it’s immutable)

This classification gives some useful terminology, but it’s not perfect. In complicated data types, there may be an operation that is both a producer and a mutator, for example. Some people reserve the term *producer* only for operations that do no mutation.

## Design an Abstract Type

It's better to have **a few, simple operations** that can be combined in powerful ways, rather than lots of complex operations.

> Coherent, Adequate, Separation

Each operation should have a well-defined purpose, and should have a **coherent** behavior rather than a panoply of special cases. We probably shouldn’t add a `sum `operation to `List `, for example. It might help clients who work with lists of integers, but what about lists of strings? Or nested lists? All these special cases would make `sum `a hard operation to understand and use.

The set of operations should be **adequate** in the sense that there must be enough to do the kinds of computations clients are likely to want to do. A good test is to check that **every property of an object of the type can be extracted.** 

The **type should not mix generic and domain-specific features.** ("Separation") A `Deck `type intended to represent a sequence of playing cards shouldn’t have a generic `add `method that accepts arbitrary objects like integers or strings. Conversely, it wouldn’t make sense to put a domain-specific method like `dealCards `into the generic type `List `.

## Representation Independence

Critically, a good abstract data type should be **representation independent** . This means that the use of an abstract type is independent of its representation (the actual data structure or data fields used to implement it), so that changes in representation have no effect on code outside the abstract type itself.

You won’t be able to change the representation of an ADT at all unless i**ts operations are fully specified with preconditions and postconditions**, so that clients know what to depend on, and you know what you can safely change.

| ADT concept             | Ways to do it in Java                                        | Examples                                                     |
| :---------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| Creator operation       | Constructor                                                  | [`ArrayList()`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ArrayList--) |
| Static (factory) method | [`Collections.singletonList() `](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#singletonList-T-), [`Arrays.asList()`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#asList-T...-) |                                                              |
| Constant                | [`BigInteger.ZERO`](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html#ZERO) |                                                              |
| Observer operation      | Instance method                                              | [`List.get()`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html#get-int-) |
| Static method           | [`Collections.max()`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#max-java.util.Collection-) |                                                              |
| Producer operation      | Instance method                                              | [`String.trim()`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#trim--) |
| Static method           | [`Collections.unmodifiableList()`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#unmodifiableList-java.util.List-) |                                                              |
| Mutator operation       | Instance method                                              | [`List.add()`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html#add-E-) |
| Static method           | [`Collections.copy()`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#copy-java.util.List-java.util.List-) |                                                              |
| Representation          | `private `fields                                             |                                                              |

## Summary

- Abstract data types are **characterized by their operations.**
- Operations can be classified into **creators, producers, observers, and mutators.**
- An ADT’s specification is its set of operations and their specs.
- A good ADT is **simple, coherent, adequate, and representation-independent.**
- An ADT is tested by generating tests for each of its operations, but using the creators, producers, mutators, and observers together in the same tests.

These ideas connect to our three key properties of good software as follows:

- **Safe from bugs.** A good ADT offers a well-defined contract for a data type, so that clients know what to expect from the data type, and implementors have well-defined freedom to vary.
- **Easy to understand.** A good ADT hides its implementation behind a set of simple operations, so that programmers using the ADT only need to understand the operations, not the details of the implementation.
- **Ready for change.** Representation independence allows the implementation of an abstract data type to change without requiring changes from its clients.

