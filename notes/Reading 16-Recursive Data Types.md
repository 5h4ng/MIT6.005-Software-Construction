# Reading 16-Recursive Data Types

## Recursive Data Types

### Immutable lists

Let’s define a data type for an immutable list, `ImList<E> `. The data type has four fundamental operations:

| empty: void → ImList      | // returns an empty list                                     |
| :------------------------ | ------------------------------------------------------------ |
| cons: E × ImList → ImList | // returns a new list formed by adding an element to the front of another list |
| first: ImList → E         | // returns the first element of a list, requires the list to be nonempty |
| rest: ImList → ImList     | // returns the list of all elements of this list except for the first, requires the list to be nonempty |

These four operations have a long and distinguished pedigree. They are fundamental to the list-processing languages [Lisp ](https://en.wikipedia.org/wiki/Lisp_(programming_language))and [Scheme ](https://en.wikipedia.org/wiki/Scheme_(programming_language))(where for historical reasons they are called nil, [cons ](https://en.wikipedia.org/wiki/Cons), [car, and cdr ](https://en.wikipedia.org/wiki/CAR_and_CDR), respectively).

#### Immutable lists in Java

Interface:

```java
public interface ImList<E> {
    // TODO: ImList<E> empty()
    public ImList<E> cons(E e);
    public E first();
    public ImList<E> rest();
}
```

And we’ll write two classes that implement this interface:

- `Empty `represents the result of the *empty* operation (an empty list)
- `Cons `represents the result of a *cons* operation (an element glued together with another list)

```java
public class Empty<E> implements ImList<E> {
    public Empty() {
    }
    public ImList<E> cons(E e) {
        return new Cons<>(e, this);
    }
    public E first() {
        throw new UnsupportedOperationException();
    }
    public ImList<E> rest() {
        throw new UnsupportedOperationException();
    }
}

public class Cons<E> implements ImList<E> {
    private final E e;
    private final ImList<E> rest;

    public Cons(E e, ImList<E> rest) {
        this.e = e;
        this.rest = rest;
    }
    public ImList<E> cons(E e) {
        return new Cons<>(e, this);
    }
    public E first() {
        return e;
    }
    public ImList<E> rest() {
        return rest;
    }
}
```

So we have methods for *cons* , *first* , and *rest* , but where is the fourth operation of our datatype, *empty* ?

One way to implement *empty* is to have clients call the `Empty `class constructor to obtain empty lists. This sacrifices representation independence — clients have to know about the `Empty `class!

As we saw in [*Interfaces* ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/14-interfaces/), a better way to do it is as a static factory method that takes no arguments and produces an instance of `Empty `. We can put this static method in the `ImList `interface along with the other operations. 

We will go ahead and update our `ImList `interface with the static `empty `method:

```java
public interface ImList<E> {
    public static <E> ImList<E> empty() {
        return new Empty<>();
    }
    public ImList<E> cons(E e);
    public E first();
    public ImList<E> rest();
}
```

> Only static members need to declare their own type parameters, because they cannot access the class/interface’s generic type parameter.
>
> `public static <E>`  is the declaration

The key thing to note here is the *sharing of structure* that immutable list provides. 

#### Two classes implementing one interface

Note that this design is different from what we have seen with `List `, `ArrayList `, and `LinkedList `. `List `is an abstract data type and `ArrayList `and `LinkedList `are two *alternative* concrete representations for that datatype.

For `ImList `, the two implementations `Empty `and `Cons `*cooperate* in order to implement the datatype — you need them both.

> We don't have to know the implementation of ImList interface. It is a good example of **representation independence**.

| Java syntax                                        | Functional syntax                    | Result      |
| :------------------------------------------------- | :----------------------------------- | :---------- |
| `ImList<Integer> nil = ImList.empty();`            | *nil = empty()*                      | [ ]         |
| `nil.cons(0)`                                      | *cons(0, nil)*                       | [ 0 ]       |
| `nil.cons(2).cons(1).cons(0)`                      | *cons(0, cons(1, cons(2, nil)))*     | [ 0, 1, 2 ] |
| `ImList<Integer> x = nil.cons(2).cons(1).cons(0);` | *x = cons(0, cons(1, cons(2, nil)))* | [ 0, 1, 2 ] |
| `x.first()`                                        | *first(x)*                           | 0           |
| `x.rest()`                                         | *rest(x)*                            | [ 1, 2 ]    |
| `x.rest().first()`                                 | *first(rest(x))*                     | 1           |
| `x.rest().rest()`                                  | *rest(rest(x))*                      | [ 2 ]       |
| `x.rest().rest().first()`                          | *first(rest(rest(x)))*               | 2           |
| `x.rest().rest().rest()`                           | *rest(rest(rest(x)))*                | [ ]         |
| `ImList<Integer> y = x.rest().cons(4);`            | *y = cons(4, rest(x))*               | [ 4, 1, 2 ] |

### Recursive datatype definitions

The abstract data type `ImList `, and its two concrete classes `Empty `and `Cons `, form a *recursive* data type. `Cons `is an implementation of `ImList `, but it also uses `ImList `inside its own rep (for the `rest `field), so it recursively requires an implementation of `ImList `in order to successfully implement its contract.

To make this fact clearly visible, we’ll write a **datatype definition** :

```
ImList<E> = Empty + Cons(first:E, rest:ImList)
```

This is a recursive definition of `ImList `as a set of values. Read it like this: the set `ImList `consists of values formed in two ways: either by the `Empty `constructor, or by applying the `Cons `constructor to an element and an `ImList `. The recursive nature of the datatype becomes far more visible when written this way.

We can also write `ImList `values as terms or expressions using this definition, e.g.:

```
Cons(0, Cons(1, Cons(2, Empty)))
```

Formally, a datatype definition has:

- an **abstract datatype** on the left, defined by its **representation** (or **concrete datatype** ) on the right
- the representation consists of **variants** of the datatype separated by **+**
- each variant is a constructor with zero or more named (and typed) arguments

Another example is a binary tree:

```java
Tree<E> = Empty + Node(e:E, left:Tree<E>, right:Tree<E>)
```

## Functions over recursive datatypes

This pattern of implementing an operation over a recursive datatype by

- **declaring** the operation in the abstract datatype interface
- **implementing** the operation (recursively) in each concrete variant

is a very common and practical design pattern. It sometimes goes by the unhelpful name *interpreter pattern* .

**size : ImList → int** // returns the size of the list

and then fully specify its meaning by defining *size* for each variant of `ImList `:

> size(Empty) = 0
> size(Cons(first: E, rest: ImList)) = 1 + size(rest)

And the cases from the definition can be translated directly into Java as methods in `ImList `, `Empty `, and `Cons `:

```java
public interface ImList<E> {
    // ...
    public int size();
}

public class Empty<E> implements ImList<E> {
    // ...
    public int size() { return 0; }
}

public class Cons<E> implements ImList<E> {
    // ...
    public int size() { return 1 + rest.size(); }
}
```

### Tuning the rep

Getting the size of a list is a common operation. Right now our implementation of `size() `takes *O(n)* time. We can make it better with a simple change to the rep of the list that caches the size the first time we compute it, so that subsequently it costs only *O(1)* time.

```java
public class Cons<E> implements ImList<E> {
    private final E e;
    private final ImList<E> rest;
    private int size = 0;
    // rep invariant:
    //   e != null, rest != null, size >= 0
    //   size > 0 implies size == 1+rest.size()

    // ...
    public int size() { 
        if (size == 0) size = 1 + rest.size();
        return size;
    }
}
```

There’s something interesting happening here: this is an immutable datatype, and yet it has a mutable rep. It’s modifying its own size field, in this case to cache the result of an expensive operation. This is an example of a **beneficent mutation** , a state change that doesn’t change the abstract value represented by the object, so the type is still immutable.

### Null vs. empty

It might be tempting to get rid of the `Empty `class and just use `null `instead. Resist that temptation.

Using an object, rather than a null reference, to signal the base case or endpoint of a data structure is an example of a design pattern called *sentinel objects* . The enormous advantage that a sentinel object provides is that it acts like an object in the datatype, so you can call methods on it. So we can call the `size() `method even on an empty list. If empty lists were represented by `null `, then we wouldn’t be able to do that, and as a consequence our code would be full of tests like:

```java
if (lst != null) n = lst.size();
```

which clutter the code, obscure its meaning, and are easy to forget. Better the much simpler

```java
n = lst.size();
```

which will always work, including when an empty `lst `refers to an `Empty `object.

> Keep `null `s out of your data structures, and your life will be happier.



## Declared type vs. actual type

Now that we’re using interfaces and classes, it’s worth taking a moment to reinforce an important point about how Java’s type-checking works. In fact every statically-checked object-oriented language works this way.

There are two worlds in type checking: **compile time** before the program runs, and run time when the program is executing.

At compile time, every variable has a **declared type** , stated in its declaration. The compiler uses the declared types of variables (and method return values) to deduce declared types for every expression in the program.

At run time, every object has an **actual type** , imbued in it by the constructor that created the object. For example, `new String() `makes an object whose actual type is `String `. `new Empty() `makes an object whose actual type is `Empty `. `new ImList() `is forbidden by Java, because `ImList `is an interface — it has no object values of its own, and no constructors.

#### Backtracking search with immutability

With immutable linked lists, backtracking is easy and safe—you just “let go” of the latest step and instantly jump back to where you were before, without having to undo or clean up anything. Because each new list only adds to the front, all the old parts are automatically shared and reused, which saves a lot of memory. Plus, since nothing ever changes, it’s easy to have many searchers exploring different paths at the same time without interfering with each other.

Some practical applications of backtracking with immutable lists include:

- **Solving puzzles** (like Sudoku, N-Queens, crosswords): Each move creates a new game state; backtracking is just discarding the latest move.
- **SAT solvers** and **symbolic logic**: Trying out different variable assignments in Boolean formulas.
- **Pathfinding** in AI and games: Exploring alternative routes without destroying previously tried paths.
- **Version control systems** (like Git): Every change creates a new snapshot, old histories are shared.
- **Functional programming algorithms**: Where undo/redo or branching histories are required.

### Summary

In addition to the big idea of **recursive datatypes** , we saw in this reading:

- **datatype definitions** : a powerful way to think about abstract types, particularly recursive ones
- **functions over recursive datatypes** : declared in the specification for the type, and implemented with one case per concrete variant
- immutable lists: a classic, canonical example of an immutable datatype

## Writing a Program with ADTs

### Recipes for programming

#### Writing a procedure (a static method)

1. **Spec.** Write the spec, including the method signiture (name, argument types, return types, exceptions), and the precondition and the postcondition as Javadoc comment.
2. **Test.** Create systematic test cases and put them in a JUnit class.
   - You may have to go back and change your spec when you start to write test cases. Steps 1 and 2 iterate until you’ve got a better spec and some good test cases. 
   - Make sure at least some of your tests are *failing* at first. 
3. **Implement.** Write the body of the method. You're done when the tests are all gree in JUnit.
   - Implementing the method puts pressure on both the tests and the specs, and you may find bugs in them that you have to go back and fix. So finishing the job may require changing the implementation, the tests, and the specs, and bouncing back and forth among them.

Let’s broaden this to a recipe for

#### Writing an abstract data type:

1. **Spec.** Write specs for the operations of the datatype, including method signatures, preconditions, and postconditions.
2. **Test.** Write test cases for the ADT’s operations.
   1. Again, this puts pressure on the spec. You may discover that you need operations you hadn’t anticipated, so you’ll have to add them to the spec.
3. **Implement.** For an ADT, this part expands to:
   1. **Choose rep.** Write down the private fields of a class, or the variants of a recursive datatype. Write down the rep invariant as a comment.
   2. **Assert rep invariant.** Implement a `checkRep() `method that enforces the rep invariant. This is critically important if the rep invariant is nontrivial, because it will catch bugs much earlier.
   3. **Implement operations.** Write the method bodies of the operations, making sure to call `checkRep() `in them. You’re done when the tests are all green in JUnit.

And let’s broaden it further to a recipe for

#### Writing a program (consisting of ADTs and procedures)

1. **Choose datatypes.** Decide which ones will be mutable and which immutable.
2. **Choose procedures.** Write your top-level procedure and break it down into smaller steps.
3. **Spec.** Spec out the ADTs and procedures. Keep the ADT operations simple and few at first. Only add complex operations as you need them.
4. **Test.** Write test cases for each unit (ADT or procedure).
5. **Implement simply first.** Choose simple, brute-force representations. The point here is to put pressure on the specs and the tests, and try to pull your whole program together as soon as possible. Make the whole program work correctly first. Skip the advanced features for now. Skip performance optimization. Skip corner cases. Keep a to-do list of what you have to revisit.
6. **Reimplement and iterate and optimize.** Now that it’s all working, make it work better.

### Problem: matrix multiplication

#### Choose datatypes

Let’s call this a `MatrixExpression `. To make the definitions easier to read, we’ll abbreviate that to `MatExpr `.

Let’s define some operations:

**make** : double[][] → MatExpr
// effects: returns an expression consisting of just the given scalar

**make** : double[][] → MatExpr
// requires: array.length > 0, and array[i].lengths are equal and > 0, for all i
// effects: returns an expression consisting of just the given matrix

**times** : MatExpr × MatExpr → MatExpr
// requires: m1 and m2 are compatible for multiplication
// effects: returns m1×m2

**isIdentity** : MatExpr → boolean
// effects: returns true if the expression is the multiplicative identity

And the one we really want:

**optimize** : MatExpr → MatExpr
// effects: returns an expression with the same value, but which may be faster to compute

#### Choose a rep

This problem is a natural one for a recursive datatype.

> MatExpr = Identity + Scalar(double) + Matrix(double[][]) + Product(MatExpr, MatExpr)

```java
/** Represents an immutable expression of matrix and scalar products. */
public interface MatrixExpression {
    // ...
}

class Identity implements MatrixExpression {
    public Identity() {
    }
}

class Scalar implements MatrixExpression {
    private final double value;
    public Scalar(double value) {
        this.value = value;
    }
}

class Matrix implements MatrixExpression {
    private final double[][] array;
    // RI: array.length > 0, and all array[i] are equal nonzero length
    public Matrix(double[][] array) {
        this.array = array; // note: danger!
    }
}

class Product implements MatrixExpression {
    private final MatrixExpression m1;
    private final MatrixExpression m2;
    // RI: m1's column count == m2's row count, or m1 or m2 is scalar
    public Product(MatrixExpression m1, MatrixExpression m2) {
        this.m1 = m1;
        this.m2 = m2;
    }
}
```

#### Choose an identity

It’s always good to have a value in the datatype that represents nothing, so that we can avoid using `null `. For a matrix product, the natural choice is the identity matrix — an empty product expression is just the identity anyway. So let’s define that:

```java
/** Identity for all matrix computations. */
public static final MatrixExpression I = new Identity();
```

Unfortunately, we’ll see that this is not a perfect situation: other MatExprs might *also* be the identity.

#### Implementing `make `: use factory methods

Let’s start implementing, starting with the `make() `creators.

We don’t want to expose our concrete rep classes `Scalar `, `Matrix `, and `Product `, so that clients won’t depend on them and we’ll be able to change them later (being *ready for change* ).

So we’ll create static methods in `MatrixExpression `to implement `make() `:

```java
/** @return a matrix expression consisting of just the scalar value */
public static MatrixExpression make(double value) {
    return new Scalar(value);
}

/** @return a matrix expression consisting of just the matrix given */
public static MatrixExpression make(double[][] array) {
    return new Matrix(array);
}
```

These are called **factory methods** — static methods that play the role of constructors. The factory-method pattern is a common design pattern that you’ll see throughout object-oriented programming, in many languages.

#### Implementing `isIdentity `: don’t use `instanceof`

Now let’s implement the `isIdentity `observer. Here’s a **bad** way to do it:

```java
// don't do this
public static boolean isIdentity(MatrixExpression m) {
    if (m instanceof Scalar) {
        return ((Scalar)m).value == 1;
    } else if (m instanceof Matrix) {
        // ... check for 1s on the diagonal and 0s everywhere else
    } else ... // do the right thing for other variant classes
}
```

In general, using `instanceof `in object-oriented programming is a bad smell. Break the operation down into pieces that are appropriate for each class, and write instance methods instead:

```java
class Identity implements MatrixExpression {
    public boolean isIdentity() { return true; }
}

class Scalar implements MatrixExpression {
    public boolean isIdentity() { return value == 1; }
}

class Matrix implements MatrixExpression {
    public boolean isIdentity() { 
        for (int row = 0; row < array.length; row++) {
            for (int col = 0; col < array[row].length; col++) {
                double expected = (row == col) ? 1 : 0;
                if (array[row][col] != expected) return false;
            }
        }
        return true;
    }
}

class Product implements MatrixExpression {
    public boolean isIdentity() { 
        return m1.isIdentity() && m2.isIdentity();
    }
}
```

Implementing `isIdentity `exposes an issue that we *should* have first discovered by writing test cases: we will not always return `true `for a `Product `whose value is the identity matrix (e.g. A × A -1 ). We probably want to resolve this by *weakening* the spec of `isIdentity `.

#### Implementing `optimize `without `instanceof`

Let’s implement `optimize() `. Again, here’s a **bad** way to do it, which will quickly get us mired in weeds:

```java
// don't do this
class Product implements MatrixExpression {
    public MatrixExpression optimize() {
        if (m1 instanceof Scalar) {
            ...
        } else if (m2 instanceof Scalar) {
            ...
        }
        ...
}
```

If you find yourself writing code with `instanceof `checks all over the place, you need to take a step back and rethink the problem.

In particular, to optimize the scalars, we’re going to need two recursive helper operations, so we’ll add them to our abstract datatype:

**scalars** : MatExpr → MatExpr
// effects: returns a MatExpr with no matrices in it, only the scalars

**matrices** : MatExpr → MatExpr
// effects: returns a MatExpr with no scalars in it, only matrices in the same order they appear in the input expression

These expressions will allow us to pull the scalars out of an expression and move them together in a single multiplication expression.

```java
/** Represents an immutable expression of matrix and scalar products. */
public interface MatrixExpression {

    // ...

    /** @return the product of all the scalars in this expression */
    public MatrixExpression scalars();

    /** @return the product of all the matrices in this expression.
     * times(scalars(), matrices()) is equivalent to this expression. */
    public MatrixExpression matrices();
}
```

Now we’ll implement them as expected:

```java
class Identity implements MatrixExpression {
    public MatrixExpression scalars() { return this; }
    public MatrixExpression matrices() { return this; }
}

class Scalar implements MatrixExpression {
    public MatrixExpression scalars() { return this; }
    public MatrixExpression matrices() { return I; }
}

class Matrix implements MatrixExpression {
    public MatrixExpression scalars() { return I; }
    public MatrixExpression matrices() { return this; }
}

class Product implements MatrixExpression {
    public MatrixExpression scalars() {
        return times(m1.scalars(), m2.scalars());
    }
    public MatrixExpression matrices() {
        return times(m1.matrices(), m2.matrices());
    }
}
```

With these helper functions, `optimize() `can just separate the scalars and the matrices:

```java
class Identity implements MatrixExpression {
    public MatrixExpression optimize() { return this; }
}

class Scalar implements MatrixExpression {
    public MatrixExpression optimize() { return this; }
}

class Matrix implements MatrixExpression {
    public MatrixExpression optimize() { return this; }
}

class Product implements MatrixExpression {
    public MatrixExpression optimize() {
        return times(scalars(), matrices());
    }
}
```

> Example codes: https://github.com/mit6005/S14-L14-programming-with-adts/tree/master
