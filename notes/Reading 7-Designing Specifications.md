# Designing Specifications

## Introduction

In this reading we’ll look at different specs for similar behaviors, and talk about the tradeoffs between them. We’ll look at three dimensions for comparing specs:

- How **deterministic** it is. Does the spec define only a single possible output for a given input, or allow the implementor to choose from a set of legal outputs?
- How **declarative** it is. Does the spec just characterize *what* the output should be, or does it explicitly say *how* to compute the output?
- How **strong** it is. Does the spec have a small set of legal implementations, or a large set?

Not all specifications we might choose for a module are equally useful, and we’ll explore what makes some specifications better than others.

## Deterministic vs. underdetermined specs

This specification is **deterministic**: when presented with a state satisfying the precondition, the outcome is completely determined.

## Declarative vs. operational specs

Roughly speaking, there are two kinds of specifications. *Operational* specifications give a series of steps that the method performs; pseudocode descriptions are operational. *Declarative* specifications don’t give details of intermediate steps. Instead, they just give properties of the final outcome, and how it’s related to the initial state.

## Stronger vs. weaker specs

Suppose you want to change a method – either how its implementation behaves, or the specification itself. There are already clients that depend on the method’s current specification. How do you compare the behaviors of two specifications to decide whether it’s safe to replace the old spec with the new spec?

A specification S2 is stronger than or equal to a specification S1 if

- S2’s precondition is weaker than or equal to S1’s,
  and
- S2’s postcondition is stronger than or equal to S1’s, for the states that satisfy S1’s precondition.

If this is the case, then an implementation that satisfies S2 can be used to satisfy S1 as well, and it’s safe to replace S1 with S2 in your program.

## Designing good specifications

- The specification should be coherent: do one thing at a time
- The results of a call should be informative
- The specification should be strong enough 
- The specification should also be weak enough: should not guarantee anything
- The specification should use **abstract types** where possible: Use `List` or `Set` rather than `ArrayList` or `HashSet`

## About access control

### Packages

A *package* is a grouping of related types providing access protection and name space management. Note that *types* refers to classes, interfaces, enumerations, and annotation types. Enumerations and annotation types are special kinds of classes and interfaces, respectively, so *types* are often referred to in this lesson simply as *classes and interfaces*.

```
LibraryApp/
│
├── src/
│   ├── libraryapp/
│   │   ├── Main.java               
│   │
│   ├── libraryapp/model/
│   │   └── Book.java              
│   │
│   ├── libraryapp/service/
│   │   └── LibraryService.java    
│   │
│   ├── libraryapp/utils/
│   │   └── StringUtils.java       
│
└── README.md
```

### Controlling Access to Members of a Class

We have been using *public* for almost all of our methods, without really thinking about it. The decision to make a method public or private is actually a decision about the contract of the class. Public methods are freely accessible to other parts of the program. Making a method public advertises it as a service that your class is willing to provide. If you make all your methods public — including helper methods that are really meant only for local use within the class — then other parts of the program may come to depend on them, which will make it harder for you to change the internal implementation of the class in the future. Your code won’t be as **ready for change** .

Making internal helper methods public will also add clutter to the visible interface your class offers. Keeping internal things *private* makes your class’s public interface smaller and more coherent (meaning that it does one thing and does it well). Your code will be **easier to understand** .

We will see even stronger reasons to use *private* in the next few classes, when we start to write classes with persistent internal state. Protecting this state will help keep the program **safe from bugs** .

### About static vs. instance methods

Static methods are not associated with any particular instance of a class, while *instance* methods (declared without the `static `keyword) must be called on a particular object.