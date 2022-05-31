package ru.johnspade.csv3s.codecs

import ru.johnspade.csv3s.core.CSV
import ru.johnspade.csv3s.codecs.StringDecoder.typeError

object instances
    extends FieldEncoderInstances
    with FieldDecoderInstances
    with RowEncoderInstances
    with RowDecoderInstances:
  given optStringEncoder[A](using stringEncoder: StringEncoder[A]): StringEncoder[Option[A]] =
    _.map(stringEncoder.encode).getOrElse("")
  given optStringDecoder[A](using stringDecoder: StringDecoder[A]): StringDecoder[Option[A]] = s =>
    if (s.isEmpty) Right(Option.empty[A]) else stringDecoder.decode(s).map(Some.apply)

  given StringEncoder[String] = identity(_)
  given StringDecoder[String] = s => Right(s)

  given StringEncoder[Int] = _.toString
  given StringDecoder[Int] = s => s.trim.toIntOption.toRight(typeError(s, "Int"))

  given StringEncoder[Long] = _.toString
  given StringDecoder[Long] = s => s.trim.toLongOption.toRight(typeError(s, "Long"))

  given StringEncoder[Float] = _.toString
  given StringDecoder[Float] = s => s.trim.toFloatOption.toRight(typeError(s, "Float"))

  given StringEncoder[Double] = _.toString
  given StringDecoder[Double] = s => s.trim.toDoubleOption.toRight(typeError(s, "Double"))

  given StringEncoder[Boolean] = _.toString
  given StringDecoder[Boolean] = s => s.trim.toBooleanOption.toRight(typeError(s, "Boolean"))
