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

```
Tree<E> = Empty + Node(e:E, left:Tree<E>, right:Tree<E>)
```



## Functions over recursive datatypes





## Writing a Program with ADTs

