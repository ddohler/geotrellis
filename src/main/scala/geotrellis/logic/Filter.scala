package geotrellis.logic

import geotrellis._
import geotrellis.process._


//case class Filter[A] (ops:[Op[Seq[A]]], condition:(A) -> Boolean) extends Op1(ops) ({
//  (ops) => Result(ops.filter(ops))
//})

//case class AsOption[A]( condition:Op[Boolean], value:A ) extends Op1(condition) ({
//  (condition) => if (condition) Result(Some(value)) else Result(None)
//})

object Filter {
  def apply[A](ops:Op[Seq[A]], condition:(A) => Boolean) = ops.map(_.filter(condition))

  def apply[A:Manifest](opsOp:Op[Seq[A]], condition:(A) => Op[Boolean]):Op[Seq[A]] = 
    opsOp.flatMap (
      (seq) => {
        seq.foldLeft (Literal(Seq[A]()):Operation[Seq[A]]) { 
          ((sum:Op[Seq[A]],v:A) => {
            for( s <- sum;
                 bool <- condition(v) ) yield if (bool) s :+ v else s
          })} 
      })

}

