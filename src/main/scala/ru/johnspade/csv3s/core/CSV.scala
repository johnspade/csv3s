package ru.johnspade.csv3s.core

sealed trait CSV
object CSV:
  final case class Complete(headers: Headers, rows: Rows) extends CSV:
    def stripTrailingRow: Complete =
      this.copy(rows = rows.stripTrailingRow)

  final case class Rows(rows: List[Row]) extends CSV:
    def stripTrailingRow: Rows = {
      val initial =
        if (rows.isEmpty)
          List.empty
        else
          rows.init
      Rows(initial)
    }

  final case class Headers(l: Seq[Header]) extends CSV
  final case class Header(value: String)   extends CSV

  final case class Row(l: Seq[Field]) extends CSV
  final case class Field(x: String)   extends CSV
