package ru.johnspade.csv3s.printer

import ru.johnspade.csv3s.core.CSV
import ru.johnspade.csv3s.parser.LiveCsvParser

trait CsvPrinter:
  def print(csv: CSV): String

object CsvPrinter:
  private def escapedIfNecessary(
      string: String,
      stringsToEscape: Set[String],
      escape: String,
      surround: String
  ) =
    if (stringsToEscape.exists(string.contains(_)))
      val escapedString = string.replace(surround, escape + surround)
      surround + escapedString + surround
    else string

  def generic(
      columnSeparator: String,
      rowSeparator: String,
      escape: String,
      surround: String,
      additionalEscapes: Set[String] = Set.empty[String]
  ): CsvPrinter =
    new CsvPrinter:
      override def print(csv: CSV): String = csv match
        case CSV.Field(text) =>
          escapedIfNecessary(
            text,
            Set(columnSeparator, rowSeparator, escape, surround) ++ additionalEscapes,
            escape,
            surround
          )
        case CSV.Header(text) =>
          escapedIfNecessary(
            text,
            Set(columnSeparator, rowSeparator, escape, surround) ++ additionalEscapes,
            escape,
            surround
          )
        case CSV.Row(xs)                 => xs.map(print).mkString(columnSeparator)
        case CSV.Headers(xs)             => xs.map(print).mkString(columnSeparator)
        case CSV.Rows(xs)                => xs.map(print).mkString(rowSeparator)
        case CSV.Complete(headers, body) => print(headers) + rowSeparator + print(body)

  def withSeparator(separator: Char) = generic(separator.toString, "\n", "\"", "\"", Set("\r"))

  def default: CsvPrinter = withSeparator(',')
