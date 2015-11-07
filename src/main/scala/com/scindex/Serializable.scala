package com.scindex

trait Serializable {
  def dump: Array[Byte]
}