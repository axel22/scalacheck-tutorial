package pp.scalacheck

import org.scalacheck._
import Prop.forAll

object MapSpecification extends Properties("Map") {

  property("+ and size") = forAll { (m: Map[Int, Int], key: Int, value: Int) =>
    (m + (key -> value)).size == (m.size + 1)
  }
  
}