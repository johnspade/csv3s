package ru.johnspade.csv3s.parser

import ru.johnspade.csv3s.parser.CsvParser
import ru.johnspade.csv3s.core.*
import zio.Chunk

val LiveCsvParser = CsvParser(',')
def parseComplete(text: String, parser: CsvParser = LiveCsvParser) =
  parser.`complete-file`.parseString(text)

def parseRow(text: String, parser: CsvParser = LiveCsvParser) =
  parser.record.parseString(text)
