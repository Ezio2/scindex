package com.scindex

import com.scindex.util.TimeMeasure
import org.scalatest.FunSuite
import Conversions._
import scala.sys.process._
import java.nio.file.Files

class ForwardIndexTest extends FunSuite {
  val tempDir = Files.createTempDirectory("forwardIndex")
  val index = new ForwardIndex[Long, String](tempDir.toString, 1000, 3600000)
  test("basic operation"){
    val num = 100000
    val data1 = (1 to num).map(x => x.toLong -> x.toString).toMap
    val data2 = ((num + 1) to (num * 2)).map(x => x.toLong -> x.toString).toMap
    val data = data1 ++ data2
    val (setT, _) = TimeMeasure.timeMeasure {
      data1.foreach{
        case (k, v) => index.set(k, v)
      }
    }
    info(s"set $num items cost $setT millis")
    val (msetT, _) = TimeMeasure.timeMeasure {
      index.mset(data2)
    }
    info(s"mset $num items cost $msetT millis")

    val data3 = (-(num - 1).toLong to (num * 3)).map(x => x -> x.toString).toMap
    val keys3 = data3.keys.toVector
    val (getT, _) = TimeMeasure.timeMeasure {
      keys3.foreach(x => assert(index.get(x) == data.get(x)))
    }
    info(s"get ${4 * num} items cost $getT millis")

    val (mgetT, _) = TimeMeasure.timeMeasure {
      index.mget(keys3).foreach {
        case (k, v) => assert(v == data.get(k))
      }
    }
    info(s"mget ${4 * num} items cost $mgetT millis")

    Seq("rm", "-rf", tempDir.toString).!!

  }

  test("ttl") {
    val tempDir1 = Files.createTempDirectory("forwardIndex")
    val index1 = new ForwardIndex[Long, String](tempDir1.toString, 1000, 2000)

    index1.set(1L, "test")
    Thread.sleep(3000)
    assert(index1.get(1L).isEmpty)

    Seq("rm", "-rf", tempDir1.toString).!!
  }

}
