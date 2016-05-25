package minitest

import scala.util.{Failure, Success, Try}

trait MiniTest{

  sealed trait Result
  case object SuccessRes extends Result
  case class FailureRes(reason: String) extends Result
  case class PendingRes(reason: String) extends Result

  private var befores = List.empty[() ⇒ Unit]
  private var afters  = List.empty[() ⇒ Unit]

  def before(f: ⇒ Unit): Unit =
    befores = befores :+ (() ⇒ f)

  def after(f: ⇒ Unit): Unit =
    afters  = afters :+ (() ⇒ f)

  private def withCallbacks[R](f: ⇒ R): R = {
    befores foreach (_.apply())
    val res = f
    afters foreach (_.apply())
    res
  }

  def assertEq[T](actual: ⇒ T, expected: T): Result =
    withCallbacks {
      if (actual == expected) SuccessRes
      else FailureRes(s"Expected $expected, got $actual")
    }

  def assertNotEq[T](actual: ⇒ T, notExpected: T): Result =
    withCallbacks {
      if (actual != notExpected) SuccessRes
      else FailureRes(s"Did not expect $notExpected")
    }

  def pending(reason: String): Result =
    PendingRes(reason)

  implicit class AssertX[T](t1: T){
    def ===(t2: T) = assertEq(t1, t2)
    def !==(t2: T) = assertNotEq(t1, t2)
  }

  def test(name: String)(f: ⇒ Result){
    Try(f) match {
      case Success(SuccessRes) ⇒
        println(s"$name: Success")
      case Success(FailureRes(reason)) ⇒
        println(s"$name: Failure: $reason")
      case Success(PendingRes(reason)) ⇒
        println(s"($name pending because $reason)")
      case Failure(th) ⇒
        println(s"$name: Exception: ${th.getMessage}")
    }
  }
}