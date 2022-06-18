# csv3s

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.johnspade/csv3s_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ru.johnspade/csv3s_3)

csv3s is a CSV Library for Scala 3 inspired by [kantan.csv](https://github.com/nrinaudo/kantan.csv) and [cormorant](https://github.com/davenverse/cormorant).

Built with [zio-parser](https://github.com/zio/zio-parser) and [Magnolia](https://github.com/softwaremill/magnolia).

## Setup

```scala
libraryDependencies += "ru.johnspade" %% "csv3s" % "<latest version in badge>"
```

## How to use

Parse a CSV row:

```scala
import ru.johnspade.csv3s.parser.*

parseRow("yellow,green,blue")
// val res0: 
//   Either[zio.parser.Parser.ParserError[String], ru.johnspade.csv3s.core.CSV.Row] = 
//     Right(Row(Chunk(Field(yellow),Field(green),Field(blue))))
```

You can also parse a complete CSV string with `parseComplete`.

Print a CSV row:

```scala
import ru.johnspade.csv3s.printer.CsvPrinter
import ru.johnspade.csv3s.core.CSV.*

CsvPrinter.default.print(Row(List(Field("yellow"), Field("green"), Field("blue"))))
// val res1: String = yellow,green,blue
```

Decode and encode case classes:

```scala
import ru.johnspade.csv3s.codecs.*
import ru.johnspade.csv3s.codecs.instances.given
import ru.johnspade.csv3s.parser.*
import ru.johnspade.csv3s.printer.CsvPrinter

case class Person(name: String, age: Int)
given decoder: RowDecoder[Person] = RowDecoder.derived
given encoder: RowEncoder[Person] = RowEncoder.derived

val parsed = parseRow("Bob,33")

parsed.map(decoder.decode(_))
// Right(Right(Person(Bob,33))): 
//   scala.util.Either[zio.parser.Parser.ParserError[java.lang.String], scala.util.Either[ru.johnspade.csv3s.codecs.DecodeError, Person]]


CsvPrinter.default.print(encoder.encode(Person("Bob", 33)))
// Bob,33: scala.Predef.String
```