package ru.johnspade.csv3s.parser

import zio.{Chunk, NonEmptyChunk}
import zio.parser._
import ru.johnspade.csv3s.core.CSV._
import ru.johnspade.csv3s.core.CSV

class CsvParser(val separator: Char):
  private val dquote: Char = '\"'
  private val cr: Char     = '\r'
  private val lf: Char     = '\n'

  private val dquoteChar = Syntax.charIn(dquote)
  private val DQUOTE     = dquoteChar.unit(dquote)
  private val TWO_DQUOTE = dquoteChar ~ dquoteChar
  private val CR         = Syntax.charIn(cr)
  private val LF         = Syntax.charIn(lf)
  private val CRLF       = CR ~ LF

  val PERMISSIVE_CRLF: Syntax[String, Char, Char, Chunk[Char], Chunk[Char]] =
    (CRLF.transform[Chunk[Char], Chunk[Char]](
      { case (c1, c2) => Chunk(c1, c2) },
      c => (c.head, c(1))
    ) | LF.transform[Chunk[Char], Chunk[Char]](
      c => Chunk(c),
      c => c.head
    ) | CR.transform[Chunk[Char], Chunk[Char]](
      c => Chunk(c),
      c => c.head
    )).named("PERMISSIVE_CRLF")

  val SEPARATOR = Syntax.charIn(separator).named("SEPARATOR")

  private val TEXTDATA = Syntax
    .charNotIn(dquote.toString + separator.toString + cr.toString + lf.toString)
    .named("TEXTDATA")

  val escaped =
    (DQUOTE ~> (TEXTDATA | SEPARATOR | CR | LF | TWO_DQUOTE.map(_ => dquote)).*.map(_.mkString)
      .map(CSV.Field.apply) <~ DQUOTE)
      .named("escaped")

  val `non-escaped` = TEXTDATA.*.map(_.mkString)
    .map(CSV.Field.apply)
    .named("non-escaped")

  val field = (escaped | `non-escaped`).named("CSV.Field")

  val name = field
    .map(f => CSV.Header(f.x))
    .named("CSV.Header")

  val header = name
    .repeatWithSep(SEPARATOR.unit(separator))
    .map(chunk => CSV.Headers(Seq.from(chunk)))
    .named("CSV.Headers")

  val record = field
    .repeatWithSep(SEPARATOR.unit(separator))
    .map(chunk => CSV.Row(Seq.from(chunk)))
    .named("CSV.Row")

  val fileBody = (record.repeatWithSep(PERMISSIVE_CRLF.unit(Chunk.empty)) <~ PERMISSIVE_CRLF.?.unit(None))
    .map(chunk => CSV.Rows(chunk.toList))
    .named("CSV.Rows")

  val `complete-file` = ((header <~ PERMISSIVE_CRLF.unit(Chunk.empty)) ~ fileBody)
    .map { case (header, rows) =>
      CSV.Complete(header, rows)
    }
    .named("CSV.Complete")
