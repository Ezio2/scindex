package com.scindex

import akka.actor.ActorSystem
import com.scindex.cache.ForwardCache
import com.scindex.db.{ForwardClient, TimeWindowClient}
import com.scindex.util.Done

import scala.concurrent.duration._


class ForwardIndex[A: TypeAlias.L, B: TypeAlias.L](directory: String, cacheSize: Int, ttl: Int,
  interval: FiniteDuration = 5.minutes, sleepTime: FiniteDuration = 5.seconds, batchNum: Int = 1000)(
  implicit a: Deserializable[A], b: Deserializable[B]) {

  private val cache = new ForwardCache[A, B](cacheSize)
  private val client = new ForwardClient[A, B](directory + "/index")
  private val timeWindowClient = new TimeWindowClient[A, B](directory + "/timeWindowIndex", ttl)


  private val system = ActorSystem(this.getClass.getSimpleName)

  import system.dispatcher

  system.scheduler.schedule(2.seconds, interval) {
    Done.promiseTrue {
      timeWindowClient.get(batchNum).map {
        case (key, value, time) =>
          remove(key)
          timeWindowClient.remove(key, value, time)
          1
      }.isEmpty
    }
  }


  def get(key: A): Option[B] = cache.get(key) match {
    case Some(value) => Some(value)
    case None =>
      val v = client.get(key)
      v.foreach(cache.put(key, _))
      v
  }

  def mget(keys: Vector[A]): Map[A, Option[B]] = keys.par.map(k => k -> get(k)).seq.toMap

  def set(key: A, value: B): Unit = {
    timeWindowClient.set(key, value, System.currentTimeMillis)
    client.set(key, value)
    cache.putIfKeyPresent(key, value)
  }

  def mset(items: Map[A, B]) = items.par.foreach(x => set(x._1, x._2))

  def remove(key: A) = {
    cache.remove(key)
    client.remove(key)
  }

  def close() = system.shutdown()


}
