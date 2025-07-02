# Reading 15-Equality

## Three Ways to Regard Equality

**Using an abstraction function** . Recall that an abstraction function f: R → A maps concrete instances of a data type to their corresponding abstract values. To use f as a definition for equality, we would say that a equals b if and only if f(a)=f(b).

**Using a relation** . An *equivalence* is a relation E ⊆ T x T that is:

- reflexive: E(t,t) ∀ t ∈ T
- symmetric: E(t,u) ⇒ E(u,t)
- transitive: E(t,u) ∧ E(u,v) ⇒ E(t,v)

To use E as a definition for equality, we would say that a equals b if and only if E(a,b).

These two notions are equivalent. An equivalence relation induces an abstraction function (the relation partitions T, so f maps each element to its partition class). The relation induced by an abstraction function is an equivalence relation (check for yourself that the three properties hold).

A third way we can talk about the equality between abstract values is in terms of what an outsider (a client) can observe about them:

**Using observation** . We can say that two objects are equal when they cannot be distinguished by observation – every operation we can apply produces the same result for both objects. Consider the set expressions {1,2} and {2,1}. Using the observer operations available for sets, cardinality |…| and membership ∈, these expressions are indistinguishable:

- |{1,2}| = 2 and |{2,1}| = 2
- 1 ∈ {1,2} is true, and 1 ∈ {2,1} is true
- 2 ∈ {1,2} is true, and 2 ∈ {2,1} is true
- 3 ∈ {1,2} is false, and 3 ∈ {2,1} is false
- … and so on

In terms of abstract data types, “observation” means calling operations on the objects. So two objects are equal if and only if they cannot be distinguished by calling any operations of the abstract data type.