package ru.johnspade.csv3s.codecs

abstract class Error(message: String) extends Exception(message) with Product with Serializable:
  override final def toString: String = productPrefix + ": " + getMessage

/** Parent type for all errors that can occur while dealing with CSV data.
  *
  * [[ReadError]] is split into two main error types:
  *   - [[DecodeError]]: errors that occur while decoding a cell or a row.
  *   - [[ParseError]]: errors that occur while parsing raw data into CSV.
  */
sealed abstract class ReadError(msg: String) extends Error(msg)

/** Parent type for all errors that can occur while decoding CSV data. */
enum DecodeError(msg: String) extends ReadError(msg):
  case OutOfBounds(index: Int)    extends DecodeError(s"${index.toString} is not a valid index")
  case TypeError(message: String) extends DecodeError(message)

/** Parent type for all errors that can occur while parsing CSV data. */
enum ParseError(msg: String) extends ReadError(msg):
  case NoSuchElement            extends ParseError("trying to read from an empty reader")
  case IOError(message: String) extends ParseError(message)
