# OOP verse Functional Programming

Suppose there are types and high level functions. 
We need implementations corresponding to each type and operation. 
OOP language like java assume that the logic related to the type is related naturally. 
It is easy to define methods within same class in OOP language. 
So, it is easy to implement new type with functions. Just define new class. 
But, if new high level function is needed, OOP lang should modify all class to implement new method.

ML kinds language is different. There is no class with method. Type and function are distinguished.
These kinds of language define one function to implement various actions dealing with various types.
And, they apply intended actions for all types by using pattern matching in function body.
So, it is easy to implement new function. Just define new function.
But, if new type is needed, ML kinds language should modify all function to deal with new type.

# Visitor pattern

Visitor pattern in OOP is mimic style of functional programming. 
By using this pattern, we can easily add new high level function without modifying all classes.

This is a example of how computer science solve problems with the layer of indirection.
```
java
interface Visitor {
    void actionChild1(Child1 child1);
    void actionChild2(Child2 child2);
}

abstract class Base {
    abstract void accept(Visitor vistior);
}

class Child1 extends Base {
    @Override
    void accept(Visitor visitor){
        visitor.actionChild1(this);
    }
}

class Child2 extends Base {
    @Override
    void accept(Visitor visitor){
        visitor.actionChild2(this);
    }
}

class Visitor1 implements Visitor{
    void actionChild1(Child1 child1) {...}
    void actionChild2(Child2 child2) {...}
}

class Visitor2 implements Visitor{
    void actionChild1(Child1 child1) {...}
    void actionChild2(Child2 child2) {...}
}

...

class VisitorN implements Visitor{
    void actionChild1(Child1 child1) {...}
    void actionChild2(Child2 child2) {...}
}

```
# Caution to Visitor Pattern
As Visitor pattern mimics functional programming, 
it is directly exposed to the weaknesses of functional programming. 
If a new class is added, all implementations of the visitor classes must be updated to reflect this. 

Also, due to the encapsulation in OOP, 
the visitor may not have access to the private fields or methods necessary for its operation. 
This can limit the functionality and flexibility of the Visitor pattern.
