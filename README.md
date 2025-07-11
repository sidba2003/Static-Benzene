This interpreted implementation of Benzene uses a Top-Down parser to translate Benzene source code into an Abstract Syntax Tree which is then typechecked and interpreted.

The typechecker / interpreter both use the Visitor Design Pattern as it allowed me to easily add support for new tree nodes.


# Usage

## Types
Types in Benzene are surrounded by `<<` and `>>`.

The primitive types are `<<string>>`, `<<boolean>>`, `<<number>>` and `<<nil>>`.

Please note, the `<<number>>` type is used to store all the integer types, such as float, integer, and double.

## Variable Declarations and Types
Variables are declared by adding the `var` keyword before an identifier. The variables do not need to be intialised during decalration. For example, 
```
  var x: <<nil>>;
  var x: <<number>> = 24;
```
Are both examples of valid Benzene code.

If a value is not initialised during variable declaration, then the nil value is assigned to variables by default.
```
  var x: <<number>>;  // invalid code, as nil will be assigned to x by default and <<number>> type does not match it
  var x: <<nil>>;  // valid Benzene code
```


## Operations

Benzene supprots various types of operations, which include, `+`, `-`, `/`, `*` and the following comparator operators, `==`, `<=`, `<`, `>=`, `>`, `!=`.
Furthermore, numbers are implicitly converted to string when adding the two.

For examples,
```
  var x: <<string>> = "Hello";
  var y: <<string>> = "world";
  print x + y; // this is valid code (outputs, Helloworld)

  var z: <<number>> = 1;
  print x + z; // this is valid code too (outputs, Hello1)
```

## Functions and Statements
Single line `comments` are applied using the `//`, and multi line comments are applied this way `/* ...this is a multi-line comment...**/`

`Print statement` is a native statement, and is used by writing the `print` keyword followed by the experession whose value needs to be printed.

Functions are defined using the fun keyword, the values are returned using the `return` keyword,
```
  fun main(a: <<number>>, b: <<number>>): <<number>>{
    print a + b;
    return 1;
  }
```
Furthermore, Benzene supports first class functions, this is because each function generates a dynamic type which can be re-used to assign functions to variables.

The type generated by functions is of the form, <<fn<return_type<param_type_1,...,param_type_n>>>>.

For example, the type generated by the above function is, `<<fn<number<number, number>>>>`.
```
  // this is valid
  var newFunction:<<fn<number<number, number>>>> = main
  newFunction(1, 2);
```

while and if statements follow the same syntax as in Java,
```
  var x: <<number>> = 1;
  while (x < 5){
    if (x > 2){
      print x;
    }
    else {
      print "the value is not greater than 2 yet!";
    }
    x = x + 1;
  }
```

## Classes and objects
Classes are defined using the `class` keyword and the constructors are defined using the `init` keyword. Along with this, the user can define multiple constructors (each with it's own signature), but Benzene would only treat the `last` defined constructor as a valid constructor. Furthermore, constructors cannot be called outside of class instance creation, and each constructor must return an object of type class' instance.

Benzene classes only support instance members. Hence there is no such thing as static fields or methods in Benzene classes.

Furthermore, Benzene classes are also first class citizens and generate a type of the form `<<cls<class_name>>>`. And class instances have a type of `<<class_name>>`.

Also, each class instance is made available in the class by the keyword `this`.

```
  class a{
    var name: <<string>> = '';

    init(name: <<string>>){
      this.name = name;
      return this;
    }

    getName(): <<string>>{
      return this.name;
    }
  }

  var b: <<a>> = a("Siddharth); // this is valid

  var c:<<cls<a>>> = a;  // valid
  var d: <<a>> = c("Siddharth) // this is valid too

  print d.getName();   // prints "Siddharth"
```
