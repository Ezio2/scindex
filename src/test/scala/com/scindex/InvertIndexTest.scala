package com.scindex

import java.nio.file.Files

import com.scindex.util.TimeMeasure
import org.scalatest.FunSuite
import Conversions._
import scala.sys.process._

class InvertIndexTest extends FunSuite {
  val tempDir = Files.createTempDirectory("invertIndex")
  val index = new InvertIndex[String, Long](tempDir.toString, 1000, 3600000)
  test("basic operation"){
    val num = 1000L
    val data1 = (1L to num).toVector
    val data2 = ((num + 1L) to (num * 2L)).toVector
    val (setT, _) = TimeMeasure.timeMeasure {
      data1.foreach(k => data1.foreach(index.add(k.toString, _)))
    }
    info(s"add ${num * num} items cost $setT millis")
    val (msetT, _) = TimeMeasure.timeMeasure {
      index.madd(data1.map(_.toString -> data1).toMap)
    }
    info(s"mset ${num * num} items cost $msetT millis")

    val (getT, _) = TimeMeasure.timeMeasure {
      data1.map(_.toString).foreach(index.get(_) == data1)
    }
    info(s"get ${num * num} items cost $getT millis")

    Seq("rm", "-rf", tempDir.toString).!!

  }

  test("ttl") {
    val tempDir1 = Files.createTempDirectory("forwardIndex")
    val index1 = new InvertIndex[Long, String](tempDir1.toString, 1000, 2000)

    index1.add(1L, "test")
    Thread.sleep(3000)
    assert(index1.get(1L).isEmpty)

    Seq("rm", "-rf", tempDir1.toString).!!
  }

}
