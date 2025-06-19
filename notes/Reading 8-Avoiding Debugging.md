# Avoiding Debugging

## First Defense: Make Bugs Impossible

- Static checking
- Dynamic checking
  - For example, Java makes array overflow bugs impossible by catching them dynamically.
  - But C/C++does not.
- Immutability 
  - immutable type: e.g. String
- Immutable references: `final`, it only makes the **reference** immutable, not necessarily the **object** that the reference points to.

## Second Defense: Localize Bugs

If we can not prevent bugs, we can try to localize them to a small part of the program, so that we don not have to look too hard to find the cause of a bug.

- Fail fast: the earlier a problem is observed (the closer to its cause), the easier it is to fix.
- defensive programming
  - e.g. checking preconditions
  - When the precondition is not satisfied, this code terminates the program by throwing an `IllegalArgumentException `exception. The effects of the caller’s bug are prevented from propagating.

## Assertions

A common practice to define a procedure for these kinds of defensive checks, usually called `assert`. This approach abstracts away from what exactly happens when the assertion fails. 

- e.g. exit, record an event in a log file, or email a report to a maintainer

### A serious problem with Java assertions is that assertions are *off by default*

If you just run your program as usual, none of your assertions will be checked! 

So you have to enable assertions explicity by passing `-ea`

It is always a good idea to have assertions turned on when you are running JUnit tests.

```java
@Test(expected=AssertionError.class)
public void testAssertionsEnable() {
	assert false;
}
```

Note that the Java `assert` statement is different from the JUnit methods `assertTrue()`, `assertEquals()`, etc.

## What to Assert

- **Check private arguments**: Method argument requirements
- **Self-check**: Method return value requirements
- Covering all cases: If a conditional statement or switch does not cover all the possible cases, it is good practice to use an assertion to block the illegal cases.

## What  Not to Assert

Runtime assertions are not free.

Never use assertions to test conditions that are external to your program, such as the existence of files, the availability of the network, or the correctness of input typed by a human user.

Assertion failures indicate bugs. External failures are not bugs, and there is no change you can make to your program in advance that will prevent them from happening. External failures should be handled using **exceptions** instead.



> From https://stackoverflow.com/questions/1276308/exception-vs-assertion/59546495#59546495:
>
> **Use assertions for internal logic** checks within your code, and normal **exceptions for error conditions outside your immediate code's control.**
>
> Don't forget that assertions can be turned on and off - if you care about things like argument validation, that should be explicit using exceptions. (**You could, however, choose to perform argument validation on *private* methods using assertions**, on the grounds that a violation at that point is due to an internal bug rather than an external error.)
>
> Alternatively it's entire reasonable (IMO) to use exceptions for everything. I personally don't use assertions much at all, but it's a matter of personal preference to some extent. (There can certainly be objective arguments for and against assertions, but it's not sufficiently clear cut to remove preference altogether.)

## Incremental Development

- [**Unit testing** ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/03-testing/#unit_testing_and_stubs): when you test a module in isolation, you can be confident that any bug you find is in that unit – or maybe in the test cases themselves.
- [**Regression testing** ](https://ocw.mit.edu/ans7870/6/6.005/s16/classes/03-testing/#automated_testing_and_regression_testing): when you’re adding a new feature to a big system, run the regression test suite as often as possible. If a test fails, the bug is probably in the code you just changed.

## Modularity & Encapsulation

**Modularity.** Modularity means dividing up a system into components, or modules, each of which can be designed, implemented, tested, reasoned about, and reused separately from the rest of the system.

**Encapsulation.** Encapsulation means building walls around a module (a hard shell or capsule) so that the module is responsible for its own internal behavior, and bugs in other parts of the system can’t damage its integrity.

- Access control
- Variable scope

### Summary

In this reading, we looked at some ways to minimize the cost of debugging:

- Avoid debugging
  - make bugs impossible with techniques like static typing, automatic dynamic checking, and immutable types and references
- Keep bugs confined
  - **failing fast with assertions** keeps a bug’s effects from spreading
  - incremental development and unit testing confine bugs to your recent code
  - **scope minimization** reduces the amount of the program you have to search



