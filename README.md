# scindex
scala convenient local forward index and invert index with ttl, support by LRU cache and rocksdb

## implementation
* cache object in memory, save to rocksdb(need serialization)
* get object from cache first, if not found, then get object from rocksdb(need deserialization)

## features

* forward index and invert index support
* fast, cache objects in memory
* auto persistence, save data to local disk in rocksdb
* controlled cache size, choose balance between efficiency and space
* support ttl
* thread safe
* easy to use, just like a normal hash map
* easy to use with your own type

## code example

### with basic type

```scala
import Conversions._

//ForwardIndex[YourType, YourType](directory, cacheSize, ttl(millis))
val f = new ForwardIndex[Long, Long]("/tmp/test/forward", 100, 2000)
f.set(100L, 100L)
println(f.get(100L)) //Some(100)
println(f.get(1000L)) //None
Thread.sleep(3000)
println(f.get(100L)) //None, ttl works
f.close()

//InvertIndex[YourType, YourType](directory, cacheSize, ttl(millis))
val i = new InvertIndex[String, Long]("/tmp/test/invert", 100, 2000)
i.add("test", 100L)
i.add("test", 10L)
println(i.get("test")) //Vector(10, 100)
println(i.get("test2")) //Vector()
i.close()
```

### custorm type

refer to [Conversions.scala](https://github.com/dup8/scindex/blob/master/src/main/scala/com/scindex/Conversions.scala), just implement your own Deserializable, Serializable, you can do without implicit

## how to get jar
in scindex root directory, run
```shell
sbt assembly
```
then you can get jar in target directory.you can import it in your own project like [this](http://stackoverflow.com/questions/18749406/how-can-i-add-unmanaged-jars-in-sbt-assembly-to-the-final-fat-jar)
## todo

I will implement a simhash server with it.
