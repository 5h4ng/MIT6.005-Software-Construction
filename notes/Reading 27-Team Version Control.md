# Reading 27-Team Version Control

## Using version control in a team

Every team develops its own standards for version control, and the size of the team and the project they’re working on is a major factor. Here are some guidelines for a small-scope team project of the kind you will undertake in 6.005:

- **Communicate.** Tell your teammates what you’re going to work on. Tell them that you’re working on it. And tell them that you worked on it. Communication is the best way to avoid wasted time and effort cleaning up broken code.
- **Write specs.** Necessary for the things we care about in 6.005, and part of good communication.
- **Write tests.** Don’t wait for a giant pile of code to accumulate before you try to test it. Avoid having one person write tests while another person writes implementation (unless the implementation is a prototype you plan to throw away). Write tests first to make sure you agree on the specs. Everyone should take responsibility for the correctness of their code.
- **Run the tests.** Tests can’t help you if you don’t run them. Run them before you start working, run them again before you commit.
- **Automate.** You’ve already automated your tests with a tool like JUnit, but now you want to automate running those tests whenever the project changes. For 6.005 group projects, we provide Didit as a way to automatically run your tests every time a team member pushes to Athena. This also removes “it worked on my machine” from the equation: either it works in the automated build, or it needs to be fixed.
- **Review what you commit.** Use `git diff --staged `or a GUI program to see what you’re about to commit. Run the tests. Don’t use `commit -a `, that’s a great way to fill your repo with `println `s and other stuff you didn’t mean to commit. Don’t annoy your teammates by committing code that doesn’t compile, spews debug output, isn’t actually used, etc.
- **Pull before you start working.** Otherwise, you probably don’t have the latest version as your starting point — you’re editing an old version of the code! You’re guaranteed to have to merge your changes later, and you’re in danger of having to waste time resolving a merge conflict.
- **Sync up.** At the end of a day or at the end of a work session, make sure everyone has pushed and pulled all the changes, you’re all at the same commit, and everyone is satisfied with the state of the project.

We don’t recommend using features like branching or rebasing for 6.005-sized projects.

We do strongly recommend working together in the same place at the same time, especially if this is your first group software engineering experience.