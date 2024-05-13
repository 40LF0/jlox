# Lox Function Call Semantic

In lox language, we can call function like this: fn(1)(2)(3). This code is not general in C style
language, but general in ML style language. ML defines a function that takes multiple arguments as a
nested function. Each function gets one argument and returns new function. And this new function
gets one argument and returns other new functions. And so goes on. After consumes all argument, last
functions complete computation.

This function style is called "currying".
