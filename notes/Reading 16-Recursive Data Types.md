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

