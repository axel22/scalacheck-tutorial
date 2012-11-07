package pp.scalacheck

import org.scalacheck._
import Prop.forAll

object ListSpecification extends Properties("List") {

  property("double reverse") = forAll { (lst: List[Int]) =>
    lst.reverse.reverse == lst
  }
  
//  property("zip reverse") = forAll { (a: List[Int], b: List[Int]) =>
//    (a.reverse zip b.reverse) == (a zip b).reverse
//  }
  
//  property("zip reverse") = forAll { (a: List[Int], b: List[Int]) =>
//    val a1 = a.take(b.length)
//    val b1 = b.take(a.length)
//    (a1.reverse zip b1.reverse) == (a1 zip b1).reverse
//  }

}