# Reading 20-Thread Safety

> Recall race conditions: multiple threads sharing the same mutable variable without coordinating what they're doing, which is unsafe because the correctness of the program may depend on accidents of timing of their low-level operations.

There are basically four ways to make variable access safe in shared-memory concurrency:

- **Confinement.** Don’t share the variable between threads. This idea is called confinement, and we’ll explore it today.
- **Immutability.** Make the shared data immutable. We’ve talked a lot about immutability already, but there are some additional constraints for concurrent programming that we’ll talk about in this reading.
- **Threadsafe data type.** Encapsulate the shared data in an existing threadsafe data type that does the coordination for you. We’ll talk about that today.
- **Synchronization.** Use synchronization to keep the threads from accessing the variable at the same time. Synchronization is what you need to build your own threadsafe data type.

## What Threadsafe Means

A data type or static method is *threadsafe* if it **behaves correctly** when used from multiple threads, **regardless of how those threads are executed**, and **without demanding additional coordination** from the calling code.

- “behaves correctly” means satisfying its specification and preserving its rep invariant;
- “regardless of how threads are executed” means threads might be on multiple processors or timesliced on the same processor;
- “without additional coordination” means that the data type can’t put preconditions on its caller related to timing, like “you can’t call `get() `while `set() `is in progress.”

> Remember [`Iterator `](https://docs.oracle.com/javase/8/docs/api/?java/util/Iterator.html)? It’s not threadsafe. `Iterator `’s specification says that you can’t modify a collection at the same time as you’re iterating over it. That’s a timing-related precondition put on the caller, and `Iterator `makes no guarantee to behave correctly if you violate it.

## Strategy 1: Confinement

Thread confinement is a simple idea: you avoid races on mutable data by keeping that data confined to a single thread. 

Local variables are always thread confined. A local variable is stored in the stack, and each thread has its own stack. 

But be careful - the variable is thread confined, but if it's an object reference, you also need to check the object it points to. If the object is mutable, then we want to check that the object is confined as well - there cannot be references to it that are reachable from any other thread.

Confinement is what makes the accesses to `n `, `i `, and `result `safe in code like this:

```java
public class Factorial {

    /**
     * Computes n! and prints it on standard output.
     * @param n must be >= 0
     */
    private static void computeFact(final int n) {
        BigInteger result = new BigInteger("1");
        for (int i = 1; i <= n; ++i) {
            System.out.println("working on fact " + n);
            result = result.multiply(new BigInteger(String.valueOf(i)));
        }
        System.out.println("fact(" + n + ") = " + result);
    }

    public static void main(String[] args) {
        new Thread(new Runnable() { // create a thread using an
            public void run() {     // anonymous Runnable
                computeFact(99);
            }
        }).start();
        computeFact(100);
    }
}
```

### Avoid Global Variables

Unlike local variables, static variables are not automatically thread confined.

If you have static variables in your program, then you hae to make an argument that only one thread will ever use them, and you have to document that fact clearly.

Better you should eliminate the static variables entirely.

Here’s an example:

```java
// This class has a race condition in it.
public class PinballSimulator {

    private static PinballSimulator simulator = null;
    // invariant: there should never be more than one PinballSimulator
    //            object created

    private PinballSimulator() {
        System.out.println("created a PinballSimulator object");
    }

    // factory method that returns the sole PinballSimulator object,
    // creating it if it doesn't exist
    public static PinballSimulator getInstance() {
        if (simulator == null) {
            simulator = new PinballSimulator();
        }
        return simulator;
    }
}
```

This class has a race in the `getInstance() `method – two threads could call it **at the same time** and end up creating two copies of the `PinballSimulator `object, which we don’t want.

To fix this, we should specify that only certain thread is allowed to call `PinballSimulator.getInstance()`. 

In general, static variables are very risky for concurrency. Consider this example:

```java
// is this method threadsafe?
/**
 * @param x integer to test for primeness; requires x > 1
 * @return true if x is prime with high probability
 */
public static boolean isPrime(int x) {
    if (cache.containsKey(x)) return cache.get(x);
    boolean answer = BigInteger.valueOf(x).isProbablePrime(100);
    cache.put(x, answer);
    return answer;
}

private static Map<Integer,Boolean> cache = new HashMap<>();
```

This function use a technique that stores answers from previous calls. But now th  `isPrime` method is not safe to call from multiple threads, and its clients may not even realize it. 

The reason is that the `HashMao` referenced by the static variable `cache` is shared by all calls to `isPrime()`, and `HashMap` is not thread safe. 

If multiple threads mutate the map at the same time, by calling `cache.put()`, then the map can become corrupted in the same way that  [the bank account became corrupted in the last reading ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/19-concurrency/#shared_memory_example). 

If you’re lucky, the corruption may cause an exception deep in the hash map, like a `Null­Pointer­Exception `or `Index­OutOfBounds­Exception `. But it also may just quietly give wrong answers, as we saw in the [bank account example ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/19-concurrency/#shared_memory_example).

> Race conditions: We need to synchronize read, modify and write

