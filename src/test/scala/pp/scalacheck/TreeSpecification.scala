package pp.scalacheck

import org.scalacheck._
import Prop.forAll

object TreeSpecification extends Properties("Tree") {

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
  
}