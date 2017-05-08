package de.ax.powermode.cache

import scala.collection.mutable

;

class Cache[K, V, I](compareValue: K => I, shoudUpdate: K => Boolean) {

  val cache: mutable.HashMap[K, (V, I)] = mutable.HashMap.empty[K, (V, I)]

  def getOrUpdate(k: K)(orUpdate: => Option[V]): Option[V] = {
    var kInfo = compareValue(k)
    val maybeV1 = if (shoudUpdate(k)) {
      None
    } else {
      cache.get(k).collect { case (v, i) => if (kInfo == i) Some(v) else None }.flatten
    }
    maybeV1 match {
      case Some(v) =>
        Some(v)
      case None =>
        val nvo = orUpdate
        nvo.map(nv => cache.put(k, (nv, kInfo)))
        nvo
    }
  }
}