package com.scindex

trait Deserializable[A] {
  def load(bytes: Option[Array[Byte]]): Option[A]
}
