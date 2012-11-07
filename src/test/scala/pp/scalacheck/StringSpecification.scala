package pp.scalacheck

import org.scalacheck._
import Prop.forAll

object StringSpecification extends Properties("String") {

  property("startsWith") = forAll { (x: String, y: String) =>
    (x + y).startsWith(x)
  }

//  property("concatenation length") = forAll { (x: String, y: String) =>
//    x.length < (x + y).length
//  }
  
//  property("concatenation length") = forAll { (x: String, y: String) =>
//    x.length <= (x + y).length
//  }
  
//  property("substring") = forAll { (a: String, b: String, c: String) =>
//    (a+b+c).substring(a.length, a.length+b.length) == b
//  }
  
}