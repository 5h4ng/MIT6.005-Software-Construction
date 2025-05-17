# Static Checking

## Types

A **type** is a set of values, along with operations that can be performed on those values.

### primitive types

- int 
- long 
- double
- char

### object types

- String

- BigInteger

- ArrayList

- Int[]

There are three different syntaxes for an operation

- As an infix, prefix, or postfix operator : ```+```
- As a method of an object: ```bigint1.add(bigint2)```
- As a function: ```Math.sin(theta)```

Some operations are **overloaded** in the sense that the same operation name is used for different types.

## Static Typing

Java is a **statically-typed language**. The type of all variables are known at compile time (before the program runs), and the compiler can therefore  deduce the types of all expressions as well.

In **dynamically-typed languages** like Python, this kind of checking is deferred until runtime.

Static typing is a particular kind of **static checking** , which means checking for bugs at compile time. 

## Static Checking, Dynamic Checking, No Checking

- **Static checking** : the bug is found automatically before the program even runs.
- **Dynamic checking** : the bug is found automatically when the code is executed.
- **No checking** : the language doesn’t help you find the error at all. You have to watch for it yourself, or end up with wrong answers.

## Arrays and Collections

Arrays are fixed-length sequence of another type T.

```java
int[] a = new int[100];
```

- indexing: `a[2]`
- assignment: `a[2]=0`
- length: `a.length `(note that this is different syntax from `String.length() `– `a.length `is not a method call, so you don’t put parentheses after it)

Lists are variable-length sequences of another type T.

```java
List<Integer> list = new ArrayList<Integer>();
```

- indexing: `list.get(2)`
- assignment: `list.set(2, 0)`
- length: `list.size()`

Note that ```List``` is an **interface**, a type that can not be constructed  directly with new, but that instead **specifies the operations that a List must provide**. 

```ArrayList``` is a class, a concrete type that provides implementations  of those operations.

Note also that we wrote ```List<Integer>``` instead of  ```List<int>```. Lists only know how to deal with object types, not primitive types. In Java, each of the primitive types (which are written in lowercase and often abbreviated, like `int`) has an equivalent object type (which is capitalized, and fully spelled out, like `Integer`). Java requires us to use these object type equivalents when we parameterize a type with **angle brackets <>**. But in other contexts, Java automatically converts between `int`and `Integer`, so we can write `Integer i = 5`without any type error.

## Iterating

```java
// find the maximum point of a hailstone sequence stored in list
int max = 0;
for (int x : list) {
    max = Math.max(x, max);
}
```

## Methods

In Java, statements generally have to be inside a method, and every method has to be in a class, so the simplest way to write a hailstone program looks like this:

```java
public class Hailstone {
  /**
   * Compute a hailstone sequence.
   * @param n  Starting number for sequence.  Assumes n > 0.
   * @return hailstone sequence starting with n and ending with 1.
   */
  public static List<Integer> hailstoneSequence(int n) {
    List<Integer> list = new ArrayList<Integer>();
    while (n != 1) {
        list.add(n);
        if (n % 2 == 0) {
            n = n / 2;
        } else {
            n = 3 * n + 1;
        }
    }
    list.add(n);
    return list;
  }
}
```

`public` means that any code, anywhere in your program, can refer to the class or method.

`static` means that the method does not take a self parameter - which in Java is implicit anyway, you will nor ever see it as a method parameter. A static method belongs to **class** instead of an instance. 

Contrast that with the `List add() `method or the `String length() `method, for example, which require an object to come first. Instead, the right way to call a static method uses the **class name** instead of an object reference:

```java
  Hailstone.hailstoneSequence(83)
```

## Mutating Values vs. Reassigning Variables

When you assign to a variable, you are changing references inside that value.

Changing is a necessary evil. Immutability (immunity from change) is a major design principle in this course. 

Immutable types are types whose values can never change once thay have been created.

Java gives us immutable references: variables that are assigned once and never reassigned:

``` java
final int n = 5;
```

```java
public static List<Integer> hailstoneSequence(int n) { 
	final List<Integer> list = new ArrayList<Integer>(); // Good practice.
```

## Documenting Assumptions

Doucimentating an assumption, and Java will check the assumption  at compile time.

- Writing down the type of a variable
- Declaring a variable final
- ...

Why? Programs have to be written with two goals in mind:

- Communicating with the computer.
- Communicating with other people.

## The Goal of 6.005

- Safe from bugs

- Easy to understand

- Ready for change

  
