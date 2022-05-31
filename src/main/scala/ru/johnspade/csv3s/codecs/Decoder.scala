package ru.johnspade.csv3s.codecs

/** Type class for types that can be decoded from other types.
  *
  * @tparam E
  *   encoded type - what to decode from.
  * @tparam D
  *   decoded type - what to decode to.
  * @tparam F
  *   failure type - how to represent errors.
  */
trait Decoder[E, D, F]:
  def decode(e: E): Either[F, D]
