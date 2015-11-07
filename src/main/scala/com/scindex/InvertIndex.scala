package com.scindex

import akka.actor.ActorSystem
import com.scindex.cache.InvertCache
import com.scindex.db.{InvertClient, TimeWindowClient}
import com.scindex.util.Done

import scala.concurrent.duration._


class InvertIndex[A: TypeAlias.L, B: TypeAlias.L](directory: String, cacheSize: Int, ttl: Int,
  interval: FiniteDuration = 5.minutes, sleepTime: FiniteDuration = 5.seconds, batchNum: Int = 1000)(
  implicit a: Deserializable[A], b: Deserializable[B]) {

  private val cache = new InvertCache[A, B](cacheSize)
  private val client = new InvertClient[A, B](directory + "/index")
  private val timeWindowClient = new TimeWindowClient[A, B](directory + "/timeWindowIndex", ttl)


  private val system = ActorSystem(this.getClass.getSimpleName)

  import system.dispatcher

  system.scheduler.schedule(2.seconds, interval) {
    Done.promiseTrue {
      timeWindowClient.get(batchNum).map {
        case (key, value, time) =>
          remove(key, value)
          timeWindowClient.remove(key, value, time)
          1
      }.isEmpty
    }
  }


  def get(key: A): Vector[B] = cache.get(key) match {
    case Some(value) => value
    case None =>
      val v = client.get(key)
      cache.put(key, v)
      v
  }

  def set(key: A, value: B): Unit = {
    timeWindowClient.set(key, value, System.currentTimeMillis)
    client.add(key, value)
    cache.putIfKeyPresent(key, value)
  }

  def mset(items: Map[A, B]) = items.par.foreach(x => set(x._1, x._2))

  def remove(key: A, value: B) = {
    cache.remove(key, value)
    client.remove(key, value)
  }

  def close() = system.shutdown()
}
