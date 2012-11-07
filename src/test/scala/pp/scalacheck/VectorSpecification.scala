package pp.scalacheck

import org.scalacheck._
import Prop.forAll

object VectorSpecification extends Properties("Vector") {

  case class Vector(x: Int, y: Int) {
    def *(s: Int) = Vector(x * s, y * s)
    def length: Double = math.sqrt(x * x + y * y)
  }

//  property("* and length") = forAll { (v: Vector, s: Int) =>
//    if (s >= 1.0) (v * s).length >= v.length
//    else (v * s).length < v.length
//  }
  
//  val ints = Gen.choose(-100, 100)
//  
//  val vectors: Gen[Vector] = for {
//    x <- ints
//	y <- ints
//  } yield Vector(x, y)
//
//  property("* and length") = forAll(vectors, ints) { (v: Vector, s: Int) =>
//    if (s >= 1.0) (v * s).length >= v.length
//    else (v * s).length < v.length
//  }
//
//  property("* and length") = forAll(vectors, ints) { (v: Vector, s0: Int) =>
//    val s = math.abs(s0)
//    if (s >= 1.0) (v * s).length >= v.length
//    else (v * s).length < v.length
//  }

	
}