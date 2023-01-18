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

  val PERMISSIVE_CRLF =
    (CRLF.transform(
      { case (c1, c2) => Chunk(c1, c2) },
      c => (c.head, c(1))
    ) | LF.transform(
      c => Chunk(c),
      c => c.head
    ) | CR.transform(
      c => Chunk(c),
      c => c.head
    )).named("PERMISSIVE_CRLF")

  val SEPARATOR = Syntax.charIn(separator).named("SEPARATOR")

  private val TEXTDATA = Syntax
    .charNotIn(dquote.toString + separator.toString + cr.toString + lf.toString)
    .named("TEXTDATA")

  val escaped =
    (DQUOTE ~> (TEXTDATA | SEPARATOR | CR | LF | TWO_DQUOTE
      .transform(_ => dquote, t => (t, t))).*.transform(xs => CSV.Field(xs.mkString), f => Chunk.from(f.x.toList))
      <~ DQUOTE)
      .named("escaped")

  val `non-escaped` = TEXTDATA.*.transform(xs => CSV.Field(xs.mkString), f => Chunk.from(f.x.toList))
    .named("non-escaped")

  val field = (escaped | `non-escaped`).named("CSV.Field")

  val name = field
    .transform(f => CSV.Header(f.x), h => CSV.Field(h.value))
    .named("CSV.Header")

  val header = name
    .repeatWithSep(SEPARATOR.unit(separator))
    .transform(chunk => CSV.Headers(Seq.from(chunk)), hs => Chunk.from(hs.l))
    .named("CSV.Headers")

  val record = field
    .repeatWithSep(SEPARATOR.unit(separator))
    .transform(chunk => CSV.Row(Seq.from(chunk)), r => Chunk.from(r.l))
    .named("CSV.Row")

  val fileBody = (record.repeatWithSep(PERMISSIVE_CRLF.unit(Chunk.empty)) <~ PERMISSIVE_CRLF.?.unit(None))
    .transform(chunk => CSV.Rows(chunk.toList), rs => Chunk.from(rs.rows))
    .named("CSV.Rows")

  val `complete-file` = ((header <~ PERMISSIVE_CRLF.unit(Chunk.empty)) ~ fileBody)
    .transform(
      { case (header, rows) =>
        CSV.Complete(header, rows)
      },
      c => (c.headers, c.rows)
    )
    .named("CSV.Complete")
