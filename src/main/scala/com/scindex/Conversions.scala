package com.scindex
import com.google.common.primitives.Longs
import com.google.common.primitives.Ints


object Conversions {

  //case class 在这里承担了相等判定，hashcode以成员变量为准
  //long
  case class LongSerializable(v: Long) extends Serializable {
    override def dump: Array[Byte] = Longs.toByteArray(v)
  }

  implicit def LongToSerializable(x: Long): LongSerializable = LongSerializable(x)


  implicit object LongDeserializable extends Deserializable[Long] {
    override def load(bytes: Option[Array[Byte]]): Option[Long] = bytes match {
      case Some(v) => Option(Longs.fromByteArray(v))
      case None => None
    }
  }

  //int
  case class IntSerializable(v: Int) extends Serializable {
    override def dump: Array[Byte] = Ints.toByteArray(v)
  }

  implicit def IntToSerializable(x: Int): IntSerializable = IntSerializable(x)


  implicit object IntDeserializable extends Deserializable[Int] {
    override def load(bytes: Option[Array[Byte]]): Option[Int] = bytes match {
      case Some(v) => Option(Ints.fromByteArray(v))
      case None => None
    }
  }


  //string
  val charset = "utf-8"
  case class StringSerializable(v: String) extends Serializable {
    override def dump: Array[Byte] = v.getBytes(charset)
  }

  implicit def StringToSerializable(x: String): StringSerializable = StringSerializable(x)


  implicit object StringDeserializable extends Deserializable[String] {
    override def load(bytes: Option[Array[Byte]]): Option[String] = bytes match {
      case Some(v) => Option(new String(v, charset))
      case None => None
    }
  }
}
