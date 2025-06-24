# Reading 11-Debugging

## Reproduce the Bug

**Always Reproduce the Bug First:**
 Before debugging, create a simple and repeatable test case that consistently triggers the bug.

**Minimize the Test Case:**
 Reduce the input that causes the bug to the smallest possible example, which makes debugging faster and easier.

**Use the Test Case as a Goal:**
 Focus on fixing the bug so that this test case passes; after the fix, add the test to your regression suite to prevent future recurrences.

**Iterative Reduction Helps:**
 Use techniques like binary search on your input to quickly narrow down the problematic case.

**Small Test Cases Save Time:**
 The effort you spend minimizing the test input pays off during debugging and when confirming your fix.

## Understand the Location and Cause of the Bug

To localize the bug and its cause, you can use the scientific method:

1. **Study the data.** Look at the test input that causes the bug, and the incorrect results, failed assertions, and stack traces that result from it.
2. **Hypothesize.** Propose a hypothesis, consistent with all the data, about where the bug might be, or where it *cannot* be. It’s good to make this hypothesis general at first.
3. **Experiment.** Devise an experiment that tests your hypothesis. It’s good to make the experiment an *observation* at first – a probe that collects information but disturbs the system as little as possible.
4. **Repeat.** Add the data you collected from your experiment to what you knew before, and make a fresh hypothesis. Hopefully you have ruled out some possibilities and narrowed the set of possible locations and reasons for the bug.

## Fix the Bug

- **Fix the root cause, not just the symptom.**

- **Check for similar issues elsewhere.**

- Add the bug’s test case to your regression test suite, and run all the tests to assure yourself that 
  - (a) the bug is fixed
  - (b) no new bugs have been introduced.

### Summary

In this reading, we looked at how to debug systematically:

- reproduce the bug as a test case, and put it in your regression suite
- find the bug using the scientific method
- fix the bug thoughtfully, not slapdash

Thinking about our three main measures of code quality:

- **Safe from bugs.** We’re trying to prevent them and get rid of them.
- **Easy to understand.** Techniques like static typing, final declarations, and assertions are additional documentation of the assumptions in your code. Variable scope minimization makes it easier for a reader to understand how the variable is used, because there’s less code to look at.
- **Ready for change.** Assertions and static typing document the assumptions in an automatically-checkable way, so that when a future programmer changes the code, accidental violations of those assumptions are detected.