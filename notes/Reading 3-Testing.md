# Reading 3-Testing

## Validation

- Formal reasoning: usually called *verification*. Verification constructs a formal proof that a program is correct.
- Code review
- Testing

## Test-first Programming

1. Write a specification for the function.
2. Write tests that exercise the specification.
3. Write the actual code. Once your code passes the tests you wrote, you're done.

The **specification** describes the input and output behavior of the function.

## Choosing Test Cases by Partitioning

We divide the input space into **subdomians**. The idea behind subdomains is to partition the input space into sets of similar inputs on which the program has similar behavior.

## Include Boundaries in the Partition

Why do bugs often happen at boundaries? One reason is that programmers often make **off-by-one mistakes** (like writing $\leq$ instead of $<$, or initializing a counter to 0 instead of 1).

## Blackbox and Whitebox Testing

- **Blackbox testing**: choosing test cases only from the specification, not the implementation of the function.
- **Whitebox testing**: choosing test cases with knowledge of how the function is actually implemented.

## Coverage

- **Statement coverage**: is every statement run by some test case?
- **Branch coverage**: for every `if` or `while` statement in the program, are both the true and the false direction takenby some test case?
- **Path coverage**: is every possible combination of branches - every path through the program - taken by some test case?

## Unit Testing and Stubs

- A test that tests an individual module, in isolation if possible, is called a **unit test**.
- The opposite of a unit test is an **integration test** , which tests a combination of modules, or even the entire program.

Unit-testing each module, in isolation as much as possible.

## Automated Testing and Regression Testing

- **Automated testing** means running the tests and checking their results automatically.
- Running all your tests after every change is called **regression testing** .
- *Test-first debugging*:  When a bug arises, immediately write a test case for it that elicits it, and immediately add it to your test suite. 

## Summary

In this reading, we saw these ideas:

- Test-first programming. Write tests before you write code.
- Partitioning and boundaries for choosing test cases systematically.
- White box testing and statement coverage for filling out a test suite.
- Unit-testing each module, in isolation as much as possible.
- Automated regression testing to keep bugs from coming back.

The topics of today’s reading connect to our three key properties of good software as follows:

- **Safe from bugs.** Testing is about finding bugs in your code, and test-first programming is about finding them as early as possible, immediately after you introduced them.
- **Easy to understand.** Testing doesn’t help with this as much as code review does.
- **Ready for change.** Readiness for change was considered by writing tests that only depend on behavior in the spec. We also talked about automated regression testing, which helps keep bugs from coming back when changes are made to code.