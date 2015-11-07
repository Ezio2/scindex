package com.scindex
import Conversions._
object Main extends App{
  val f = new ForwardIndex[Long, Long]("/tmp/test/forward", 100, 10)
  f.set(100L, 100L)
  val x: Option[Long] = f.get(100L)
  println(x)
  println(f.get(100L))
  f.close()


  val i = new InvertIndex[String, Long]("/tmp/test/invert", 100, 10)
  i.set("fuck", 100L)
  i.set("fuck", 10L)
  val y: Vector[Long] = i.get("fuck")
  println(y)
  println(i.get("fuck"))
  i.close()
}
