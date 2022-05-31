package ru.johnspade.csv3s.codecs

import magnolia1.*
import ru.johnspade.csv3s.core.CSV

type DecodeResult[A] = Either[DecodeError, A]

type StringEncoder[A] = Encoder[String, A]

type StringDecoder[A] = Decoder[String, A, DecodeError]
object StringDecoder:
  def typeError(value: String, tp: String) = DecodeError.TypeError(s"$value is not valid $tp")

type FieldEncoder[A] = Encoder[CSV.Field, A]
trait FieldEncoderInstances:
  given fromStringEncoder[A](using stringEncoder: StringEncoder[A]): FieldEncoder[A] =
    new FieldEncoder[A] {
      def encode(a: A): CSV.Field =
        CSV.Field(stringEncoder.encode(a))
    }

type FieldDecoder[A] = Decoder[CSV.Field, A, DecodeError]
trait FieldDecoderInstances:
  given fromStringDecoder[A](using stringDecoder: StringDecoder[A]): FieldDecoder[A] =
    new FieldDecoder[A] {
      def decode(e: CSV.Field): DecodeResult[A] =
        stringDecoder.decode(e.x)
    }

type RowEncoder[A] = Encoder[CSV.Row, A]
trait RowEncoderInstances:
  given fromFieldEncoder[A](using fieldEncoder: FieldEncoder[A]): RowEncoder[A] =
    new RowEncoder[A] {
      def encode(a: A): CSV.Row =
        CSV.Row(Seq(fieldEncoder.encode(a)))
    }

object RowEncoder extends ProductDerivation[RowEncoder]:
  def apply[A](using ev: RowEncoder[A]): RowEncoder[A] = ev

  override def join[A](ctx: CaseClass[Typeclass, A]): RowEncoder[A] = value =>
    if (ctx.isObject) throw new RuntimeException("Can't encode case objects")
    else
      val encodedFields = ctx.params.foldLeft(Seq.empty[CSV.Field]) { (acc, p) =>
        acc ++ p.typeclass.encode(p.deref(value)).l
      }
      CSV.Row(encodedFields)

type RowDecoder[A] = Decoder[CSV.Row, A, DecodeError]

trait RowDecoderInstances:
  given fromFieldDecoder[A](using fieldDecoder: FieldDecoder[A]): RowDecoder[A] =
    new RowDecoder[A] {
      def decode(e: CSV.Row): DecodeResult[A] =
        fieldDecoder.decode(e.l.head)
    }

object RowDecoder extends ProductDerivation[RowDecoder]:
  def apply[A](using ev: RowDecoder[A]): RowDecoder[A] = ev

  override def join[A](ctx: CaseClass[Typeclass, A]): RowDecoder[A] = value =>
    ctx
      .constructEither { param =>
        param.typeclass.decode(CSV.Row(Seq(value.l(param.index))))
      }
      .left
      .map(_.head)
