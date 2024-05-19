# Scope Principle

A variable use refers to a previous declaration with the same name in the preceding scope
surrounding the expression in which the variable is used.

- To include both variable expressions and assignments, the term variable use is used instead of
  variable expression.

  It means that it is an expression where the variable is used.
- Preceding means that it appears before the program text.

Usually, a declaration preceded 'in text' precedes the code that uses it 'in time', but this is not
always the case.

A function can postpone the execution of a chunk of code so that dynamic temporal execution does not
reflect static textual ordering.

In Lox, the environment acts like a scope that changes over time.

For closures, when a function is declared, it must capture a reference pointing to the current
environment.