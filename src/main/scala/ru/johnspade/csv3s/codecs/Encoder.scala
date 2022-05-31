package ru.johnspade.csv3s.codecs

/** Type class for types that can be encoded into others.
  *
  * @tparam E
  *   encoded type - what to encode to.
  * @tparam D
  *   decoded type - what to encode from.
  */
trait Encoder[E, D]:
  def encode(d: D): E
