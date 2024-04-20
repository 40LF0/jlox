# Introduction
This document will cover related information about converting tokens into richer, 
more complex representations.

To create such an expression, the goal of the document is to define the expression.

1. The discussion of code representation 
2. The explanation of the formal grammar theory
3. The difference between functional programming and Object oriented programming
4. Related design patterns

## Code Representation
The code representation should be easy to write with a parser and to use with an interpreter.

Let's think about the following expression. How we evaluate this expression?

`1 + 2 * 3 -4`

The operator evaluation order is multiplication/division before addition/subtraction.
The priority can be visualized in tree form as follows. 
Leaf nodes are numbers, and internal nodes are operators branched to each operand.

         -               -                -              3
       /   \           /   \            /   \              
      +     4    ->    +     4    ->   7     4    -> 
     / \              / \     
    1   *            1   6 
       / \        
      2   3 

In order to evaluate an arithmetic node, the subtree must be evaluated to obtain a numeric value. 
Post-order traversal is a suitable method for achieving this.

It can be inferred that the executable code expression is a grammatical structure 
in which language operators are nested.

## Context Free Grammar
TBA