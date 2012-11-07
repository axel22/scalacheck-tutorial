ScalaCheck is a tool for testing Scala and Java programs, based on property specifications and
automatic test data generation. The basic idea is that you define a property that specifies the
behaviour of a method or some unit of code, and ScalaCheck checks that the property holds.
All test data are generated automatically in a random fashion, so you don't have to worry about
any missed cases.

In this tutorial, we explain the basics of ScalaCheck, how it works and several interesting
use-cases. You can learn more about it at https://github.com/rickynils/scalacheck/wiki/User-Guide.

## Prerequisites

- Download and install Eclipse Indigo (3.7): http://www.eclipse.org/indigo/
- Download and install Scala IDE for version 2.9.x: http://scala-ide.org/download/current.html
- Download the ScalaCheck 1.10.0 testing library: https://oss.sonatype.org/index.html#nexus-search;quick~scalacheck_2.9.2

## Instructions

We first do the following preparatory steps.

1. Create a new Scala project in Eclipse.
2. Create a folder `lib`.
3. Copy the ScalaCheck 1.10.0 JAR file into the `lib` folder.
4. Go to Project -> Properties -> Java Build Path -> Add JARs and select the ScalaCheck JAR from the `lib` folder.
5. Create a source folder `src/test/scala`.
6. Create a new package `pp.scalacheck` in the source folder above.
7. Create a new file `StringSpecification.scala` in the package above.

Alternatively, you can download the entire project from: http://github.com/axel22/scalacheck-tutorial

### Testing `String`s

We will now write a few ScalaCheck tests that test certain `String` properties.
We import the contents of the ScalaCheck package and its `forAll` statement:

    package pp.scalacheck
    
    import org.scalacheck._
    import Prop.forAll

Next, we define an object `StringSpecification` which will contain multiple properties
of `String`s. The first such property will test that the concatenation of two strings
`x` and `y` always starts with the string `x`.

    object StringSpecification extends Properties("String") {
    
      property("startsWith") = forAll { (x: String, y: String) =>
        (x + y).startsWith(x)
      }
    
      // ... more properties will follow ...
      
    }

We defined the property named `"startsWith"` saying that
it must be true that for all `String`s `x` and `y` their concatenation is
starts with `x`.

Each `Properties` object is a complete application in ScalaCheck - it has a `main`
method defined, which simply tests all the properties.
This means that we can run it in Eclipse, so lets do exactly that.
We click Run -> Run As -> Scala Application to obtain the following output:

    + String.startsWith: OK, passed 100 tests.

We get an output saying that ScalaCheck generated `100` different `String`s
and that our property was true for all of them.

Lets write another property. This one states that the length of then concatenation of
two `String`s is always longer than one of those `String`s.

    property("concatenation length") = forAll { (x: String, y: String) =>
      x.length < (x + y).length
  	}

Running the test suite again we get:

    ! String.concatenation length: Falsified after 0 passed tests.
    > ARG_0: ""
    > ARG_1: ""

Ooops! ScalaCheck is telling us that this seemingly simple property of `String`s
isn't true at all. In fact, it even produces a small counterexample -- what if we
have 2 empty `String`s? In that case our property does not hold.
Let lets fix it to state that the concatenation is longer or equal to one of the
two `String`s:

    property("concatenation length") = forAll { (x: String, y: String) =>
      x.length <= (x + y).length
    }

Running the tests now yields:

    + String.startsWith: OK, passed 100 tests.
    + String.concatenation length: OK, passed 100 tests.

You can try writing a couple of your own properties.


### Testing Collections

Automatic generation of input data is particularly useful for testing collections,
so lets write a couple of collection properties.

We create a new file in `pp.scalacheck` called `ListSpecification.scala` and write
the following property saying that reversing a list twice gives back the original
list:

    object ListSpecification extends Properties("List") {
    
      property("double reverse") = forAll { (lst: List[Int]) =>
        lst.reverse.reverse == lst
      }
      
    }

ScalaCheck is smart enough to know how the generate arbitrary lists of
integers for us. Running the tests yields:

    + List.double reverse: OK, passed 100 tests.

Lets write another property involving lists, stating that reversing two lists
and zipping them is the same as first zipping them and then reversing the
resulting list of pairs (for example, for `1 :: 2 :: Nil` and `2 :: 4 :: Nil`
we get `(2, 4) :: (1, 2) :: Nil`).

    property("zip reverse") = forAll { (a: List[Int], b: List[Int]) =>
      (a.reverse zip b.reverse) == (a zip b).reverse
    }  

It's not apparent at first sight that this property does not always hold:

    ! List.zip reverse: Falsified after 3 passed tests.
    > ARG_0: List("0")
    > ARG_1: List("-1", "0")

In fact, the property always fails when the lists don't have the same length.
We can fix the property by ensuring that both lists do have the same length:

    property("zip reverse") = forAll { (a: List[Int], b: List[Int]) =>
      val a1 = a.take(b.length)
      val b1 = b.take(a.length)
      (a1.reverse zip b1.reverse) == (a1 zip b1).reverse
    }

ScalaCheck can test the properties of other collections as well. For example,
the following property of `Map`s says that after you add a key-value pair to
any `Map`, the size of the resulting map will increase:

    property("+ and size") = forAll { (m: Map[Int, Int], key: Int, value: Int) =>
      (m + (key -> value)).size == (m.size + 1)
    }

Try to find a counterexample. Then use ScalaCheck to find check if your
counterexample is similar.


### Testing Custom Datatypes

We've seen so far that ScalaCheck is pretty smart in practice with generating the
right data that can lead to counterexamples.
However, ScalaCheck cannot by default generate input data for datatypes it does not
know about.
For example, lets define a custom datatype called `Vector` which describes two-dimensional
mathematical vectors. We also define 2 operations -- scalar multiplication and vector length. 

    case class Vector(x: Int, y: Int) {
      def *(s: Int) = Vector(x * s, y * s)
      def length: Double = math.sqrt(x * x + y * y)
    }

Lets try to write a property involving this vector, for example, one that says that
if we multiply a vector with a scalar larger than `1.0`, its length will increase.
Otherwise, its length will decrease.

    property("* and length") = forAll { (v: Vector, s: Int) =>
      if (s >= 1.0) (v * s).length >= v.length
      else (v * s).length < v.length
    }

The snippet above does not compile -- instead it produces some strange error message
about not finding an implicit value:

    - could not find implicit value for parameter a1: 
	  org.scalacheck.Arbitrary[pp.scalacheck.VectorSpecification.Vector]

What the compiler is trying to tell us here is that the ScalaCheck library knows nothing
about our user-defined `Vector` datatype, so it can't generate input examples for it.

This is where ScalaCheck generators come into play.
A generator (`Gen`) is an object which describes how input data is generated for
a certain type.
ScalaCheck comes with a range of predefined generators for basic datatypes.
For example, the generator `Arbitrary.arbitrary[Double]` generates arbitrary real numbers:

    val doubles = Arbitrary.arbitrary[Double]
    
The generator `Gen.oneOf` generates the specified values, in the example below `true` or `false`:

    val booleans = Gen.oneOf(true, false)

Another predefined generator `Gen.choose` picks integer numbers within a given range:

	val twodigits = Gen.choose(-100, 100)

How to use a generator in a property? We simply have to specify it after the `forAll`:

    property("sqrt") = forAll(ints) { d =>
      math.sqrt(d * d) == d
    }

Btw, the above property is not always true. Try to figure out a counterexample.

Finally, several basic generators can be combined into more complex generators,
and this is a recommended strategy to obtain generators for more custom datatypes.
This is done using for-comprehensions which you already saw on collections:

    val vectors: Gen[Vector] =
      for {
        x <- Gen.choose(-100, 100)
        y <- Gen.choose(-100, 100)
      } yield Vector(x, y)

The above should be read as -- we define a generators called `vectors` which generates
`Vector`s as follows: use a generator `choose` to generate a value `x` in the range from
`-100` to `100`, and another generator `choose` to generate a value `y` in the same range,
then use these values to create `Vector(x, y)`.

This (intuitive) for-comprehension is translated into a chain of (unintuitive) `map`/`flatMap`
calls. Try to figure out what the desugared expression looks like!

Now that we have the generator for vectors, we can use it:

    property("* and length") = forAll(vectors, ints) { (v: Vector, s: Int) =>
      if (s >= 1.0) (v * s).length >= v.length
      else (v * s).length < v.length
    }

Alas, our property still fails because `s` might be negative. Fix it to use only
positive scalars!

We conclude this short tutorial by showing how you can write an even more complex
generator. Lets create a custom datatype for binary trees of integers:

    trait Tree
    case class Node(left: Tree, right: Tree) extends Tree
    case class Leaf(x: Int) extends Tree
  
    val ints = Gen.choose(-100, 100)
  
    def leafs: Gen[Leaf] = for {
  	  x <- ints
    } yield Leaf(x)

    def nodes: Gen[Node] = for {
      left <- trees
      right <- trees
    } yield Node(left, right)

    def trees: Gen[Tree] = Gen.oneOf(leafs, nodes)

Just like the tree datatype, its generator consists of several cases and is mutually recursive.
Try to figure out how it works.

Then define a method `depth` on `Tree`s which returns the depth of the tree.
Write a ScalaCheck test to verify if for any two trees `x` and `y`, the tree
`Node(x, y)` is deeper than both `x` and `y`.








