package ru.johnspade.csv3s

import zio.parser.*
import zio.test.*
import zio.test.TestEnvironment
import zio.{Chunk, Scope}
import zio.test.Assertion.{equalTo, isRight}
import ru.johnspade.csv3s.core.CSV.*
import ru.johnspade.csv3s.core.CSV
import ru.johnspade.csv3s.parser.LiveCsvParser

object CsvParserSpec extends ZIOSpecDefault:
  private val csvParser = LiveCsvParser

  override def spec: ZSpec[TestEnvironment with Scope, Any] = suite("CSV example")(
    test("parse a single header") {
      val basicString = "Something,"
      val expect      = CSV.Header("Something")
      assert(csvParser.name.parseString(basicString))(isRight(equalTo(expect)))
    },
    test("parse first header in a header list") {
      val baseHeader = "Something,Something2,Something3"
      val expect     = CSV.Header("Something")
      assert(csvParser.name.parseString(baseHeader))(isRight(equalTo(expect)))
    },
    test("parse a group of headers") {
      val baseHeader = "Something,Something2,Something3"
      val expect = Chunk(
        CSV.Header("Something"),
        CSV.Header("Something2"),
        CSV.Header("Something3")
      )
      val result = csvParser.name.repeatWithSep(csvParser.SEPARATOR.unit(csvParser.separator)).parseString(baseHeader)
      assert(result)(isRight(equalTo(expect)))
    },
    test("parse headers correctly") {
      val baseHeader = """Something,Something2,Something3"""
      val expect = CSV.Headers(
        Seq(
          CSV.Header("Something"),
          CSV.Header("Something2"),
          CSV.Header("Something3")
        )
      )
      val result = csvParser.header.parseString(baseHeader)
      assert(result)(isRight(equalTo(expect)))
    },
    test("parse a row correctly") {
      val singleRow = "yellow,green,blue"
      val expected = CSV.Row(
        Seq(
          CSV.Field("yellow"),
          CSV.Field("green"),
          CSV.Field("blue")
        )
      )
      assert(csvParser.record.parseString(singleRow))(isRight(equalTo(expected)))
    },
    test("parse rows correctly") {
      val csv = CSV.Rows(
        List(
          CSV.Row(Seq(CSV.Field("Blue"), CSV.Field("Pizza"), CSV.Field("1"))),
          CSV.Row(Seq(CSV.Field("Red"), CSV.Field("Margarine"), CSV.Field("2")))
        )
      )
      val csvParse = """Blue,Pizza,1
                       |Red,Margarine,2""".stripMargin
      assert(csvParser.fileBody.parseString(csvParse))(isRight(equalTo(csv)))
    },
    test("complete a csv parse") {
      val csv = CSV.Complete(
        CSV.Headers(
          Seq(CSV.Header("Color"), CSV.Header("Food"), CSV.Header("Number"))
        ),
        CSV.Rows(
          List(
            CSV.Row(Seq(CSV.Field("Blue"), CSV.Field("Pizza"), CSV.Field("1"))),
            CSV.Row(Seq(CSV.Field("Red"), CSV.Field("Margarine"), CSV.Field("2"))),
            CSV.Row(Seq(CSV.Field("Yellow"), CSV.Field("Broccoli"), CSV.Field("3")))
          )
        )
      )
      val expectedCSVString = """Color,Food,Number
                                |Blue,Pizza,1
                                |Red,Margarine,2
                                |Yellow,Broccoli,3""".stripMargin

      assert(csvParser.`complete-file`.parseString(expectedCSVString))(isRight(equalTo(csv)))
    },
    test("parse a complete csv with a trailing new line by stripping it") {
      val csv = CSV.Complete(
        CSV.Headers(
          Seq(CSV.Header("Color"), CSV.Header("Food"), CSV.Header("Number"))
        ),
        CSV.Rows(
          List(
            CSV.Row(Seq(CSV.Field("Blue"), CSV.Field("Pizza"), CSV.Field("1"))),
            CSV.Row(Seq(CSV.Field("Red"), CSV.Field("Margarine"), CSV.Field("2"))),
            CSV.Row(Seq(CSV.Field("Yellow"), CSV.Field("Broccoli"), CSV.Field("3")))
          )
        )
      )
      val expectedCSVString = """Color,Food,Number
                                |Blue,Pizza,1
                                |Red,Margarine,2
                                |Yellow,Broccoli,3
                                |""".stripMargin

      assert(
        csvParser.`complete-file`.parseString(expectedCSVString).map(_.stripTrailingRow)
      )(isRight(equalTo(csv)))
    },
    test("parse an escaped row with a comma") {
      val csv = CSV.Row(
        Seq(
          CSV.Field("Green"),
          CSV.Field("Yellow,Dog"),
          CSV.Field("Blue")
        )
      )
      val parseString = "Green,\"Yellow,Dog\",Blue"
      assert(csvParser.record.parseString(parseString))(isRight(equalTo(csv)))
    },
    test("parse an escaped row with a double quote escaped") {
      val csv = CSV.Row(
        Seq(
          CSV.Field("Green"),
          CSV.Field("Yellow, \"Dog\""),
          CSV.Field("Blue")
        )
      )
      val parseString = "Green,\"Yellow, \"\"Dog\"\"\",Blue"
      assert(csvParser.record.parseString(parseString))(isRight(equalTo(csv)))
    },
    test("parse an escaped row with embedded newline") {
      val csv = CSV.Row(
        Seq(
          CSV.Field("Green"),
          CSV.Field("Yellow\n Dog"),
          CSV.Field("Blue")
        )
      )
      val parseString = "Green,\"Yellow\n Dog\",Blue"
      assert(csvParser.record.parseString(parseString))(isRight(equalTo(csv)))
    },
    test("parse an escaped row with embedded CRLF") {
      val csv = CSV.Row(
        Seq(
          CSV.Field("Green"),
          CSV.Field("Yellow\r\n Dog"),
          CSV.Field("Blue")
        )
      )
      val parseString = "Green,\"Yellow\r\n Dog\",Blue"
      assert(csvParser.record.parseString(parseString))(isRight(equalTo(csv)))
    }
  )
