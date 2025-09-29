# Reading 25-Map, Filter, Reduce

In the reading we discuss *map/filter/reduce*, a design pattern that substantially simplifies the implementation of functions that operate over sequences of elements.

## Abstracting out control flow

### Iterator abstraction

Iterator gives you a sequence of elements from a data structure, without you having to worry about whether the data structure is a set or a token stream or a list or an array — the [`Iterator `](https://docs.oracle.com/javase/8/docs/api/?java/util/Iterator.html)looks the same no matter what the data structure is.

All loop will be identical for any type that provides an `Iterator`. 

### Map/filter/reduce abstraction

They treat the entire sequence of elements as a unit, so the programmer doesn't have to name and work with the elements individually.

### Sequences

Let’s imagine an abstract datatype `Seq<E> `that represents a *sequence* of elements of type `E `. For example, `[1, 2, 3, 4] `∈ `Seq<Integer> `.

## Map

**Map** applies a unary function to each element in the sequence and returns a new sequence containing the results, in the same order:

**map : (E → F) × Seq<‍E> → Seq<‍F>**

For example, in Python:

```python
>>> from math import sqrt
>>> map(sqrt, [1, 4, 9, 16])
[1.0, 2.0, 3.0, 4.0]
>>> map(str.lower, ['A', 'b', 'C'])
['a', 'b', 'c']
```

`map `is built-in, but it is also straightforward to implement in Python:

```python
def map(f, seq):
    result = []
    for elt in seq:
        result.append(f(elt))
    return result
```

## Functions as values

The `map` function takes a reference to a *function* as its first argument - not the result of that function.

Functions are **first-class** in Python, meaning that they can be assigned to variables, passed as parameters, used as return values, and stored in data structures. 

## Filter

Our next important sequence operation is **filter** , which tests each element with a unary predicate. Elements that satisfy the predicate are kept; those that don’t are removed. A new list is returned; filter doesn’t modify its input list.

**filter : (E → boolean) × Seq<‍E> → Seq<‍E>**

Python examples:

```python
>>> filter(str.isalpha, ['x', 'y', '2', '3', 'a']) 
['x', 'y', 'a']
>>> def isOdd(x): return x % 2 == 1
... 
>>> filter(isOdd, [1, 2, 3, 4])
[1, 3]
>>> filter(lambda s: len(s)>0, ['abc', '', 'd'])
['abc', 'd']
```

## Reduce

Our final operator, **reduce** , combines the elements of the sequence together, using a binary function. In addition to the function and the list, it also takes an *initial value* that initializes the reduction, and that ends up being the return value if the list is empty.

**reduce : (F × E → F) × Seq<‍E> × F → F**

`reduce(f, list, init) `combines the elements of the list from left to right, as follows:

> result 0 = init
> result 1 = f(result 0 , list[0])
> result 2 = f(result 1 , list[1])
> ...
> result n = f(result n-1 , list[n-1])

Adding numbers is probably the most straightforward example:

```python
>>> reduce(lambda x,y: x+y, [1, 2, 3], 0)
6
# --or--
>>> import operator
>>> reduce(operator.add, [1, 2, 3], 0)
6
```

Suppose we have a polynomial represented as a list of coefficients, a[0], a[1], ..., a[n-1], where a[i] is the coefficient of $x^i$ . Then we can evaluate it using map and reduce:

```python
def evaluate(a, x):
    xi = map(lambda i: x**i, range(0, len(a))) # [x^0, x^1, x^2, ..., x^(n-1)]
    axi = map(operator.mul, a, xi)             # [a[0]*x^0, a[1]*x^1, ..., a[n-1]*x^(n-1)]
    return reduce(operator.add, axi, 0)        # sum of axi
```

Now let’s look at a typical database query example. Suppose we have a database about digital cameras, in which each object is of type `Camera `with observer methods for its properties ( `brand() `, `pixels() `, `cost() `, etc.). The whole database is in a list called `cameras `. Then we can describe queries on this database using map/filter/reduce:

```python
# What's the highest resolution Nikon sells? 
reduce(max, 
       map(Camera.pixels, 
           filter(lambda c: c.brand() == "Nikon", cameras)
          )
      )
```

Relational databases use the map/filter/reduce paradigm (where it’s called project/select/aggregate). [SQL ](https://en.wikipedia.org/wiki/SQL)(Structured Query Language) is the *de facto* standard language for querying relational databases. A typical SQL query looks like this:

```sql
select max(pixels) from cameras where brand = "Nikon"
```

> `cameras `is a **sequence** (a list of rows, where each row has the data for one camera)
>
> `where brand = "Nikon" `is a **filter**
>
> `pixels `is a **map** (extracting just the pixels field from the row)
>
> `max `is a **reduce**

## irst-class functions in Java

**In Java, the only first-class values are primitive values (ints, booleans, characters, etc.) and object references.** 

But objects can carry functions with them, in the form of methods. So it turns out that the way to implement a first-class function, in an object-oriented programming language like Java that doesn’t support first-class functions directly, is to use an object with a method representing the function.

We’ve actually seen this before several times already:

- The `Runnable `object that you pass to a `Thread `constructor is a first-class function, `void run() `.
- The `Comparator<T> `object that you pass to a sorted collection (e.g. `SortedSet `) is a first-class function, `int compare(T o1, T o2) `.
- The `KeyListener `object that you register with the graphical user interface toolkit to get keyboard events is a bundle of several functions, `keyPressed(KeyEvent) `, `keyReleased(KeyEvent) `, etc.

This design pattern is called a **functional object** or **functor** , an object whose purpose is to represent a function.

### Lambda expressions in Java

Java’s lambda expression syntax provides a succinct way to create instances of functional objects. For example, instead of writing:

```java
new Thread(new Runnable() {
    public void run() {
        System.out.println("Hello!");
    }
}).start();
```

we can use a lambda expression:

```java
new Thread(() -> {
    System.out.println("Hello");
}).start();
```

There’s no magic here: Java still doesn’t have first-class functions. So you can only use a lambda when the Java compiler can verify two things:

1. It must be able to determine the type of the functional object the lambda will create. In this example, the compiler sees that the `Thread `constructor takes a `Runnable `, so it will infer that the type must be `Runnable `.
2. This inferred type must be *functional interface* : an interface with only one (abstract) method. In this example, `Runnable `indeed only has a single method — `void run() `— so the compiler knows the code in the body of the lambda belongs in the body of a `run `method of a new `Runnable `object.

Java provides some [standard functional interfaces ](https://docs.oracle.com/javase/8/docs/api/?java/util/function/package-summary.html)we can use to write code in the map/filter/reduce pattern, e.g.:

- [`Function `](https://docs.oracle.com/javase/8/docs/api/?java/util/function/Function.html)represents unary functions from `T `to `R`
- [`BiFunction `](https://docs.oracle.com/javase/8/docs/api/?java/util/function/BiFunction.html)represents binary functions from `T `× `U `to `R`
- [`Predicate `](https://docs.oracle.com/javase/8/docs/api/?java/util/function/Predicate.html)represents functions from `T `to boolean

So we could implement map in Java like so:

```java
/**
 * Apply a function to every element of a list.
 * @param f function to apply
 * @param list list to iterate over
 * @return [f(list[0]), f(list[1]), ..., f(list[n-1])]
 */
public static <T,R> List<R> map(Function<T,R> f, List<T> list) {
    List<R> result = new ArrayList<>();
    for (T t : list) {
        result.add(f.apply(t));
    }
    return result;
}
```

And here’s an example of using map; first we’ll write it using the familiar syntax:

```java
// anonymous classes like this one are effectively lambda expressions
Function<String,String> toLowerCase = new Function<>() {
    public String apply(String s) { return s.toLowerCase(); }
};
map(toLowerCase, Arrays.asList(new String[] {"A", "b", "C"}));
```

And with a lambda expression:

```java
map(s -> s.toLowerCase(), Arrays.asList(new String[] {"A", "b", "C"}));
// --or--
map((s) -> s.toLowerCase(), Arrays.asList(new String[] {"A", "b", "C"}));
// --or--
map((s) -> { return s.toLowerCase(); }, Arrays.asList(new String[] {"A", "b", "C"}));
```

In this example, the lambda expression is just wrapping a call to `String `’s `toLowerCase `. We can use a *method reference* to avoid writing the lambda, with the syntax `:: `. The signature of the method we refer to must match the signature required by the functional interface for static typing to be satisfied:

```java
map(String::toLowerCase, Arrays.asList(new String[] {"A", "b", "C"}));
```

In the Java Tutorials, you can read more about [**method references** ](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html)if you want the details.

Using a method reference (vs. calling it) in Java serves the same purpose as referring to a function by name (vs. calling it) in Python.