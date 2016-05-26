package lst

sealed trait Lst[+A] extends PartialFunction[Int, A] {

  // returnerer om listen er tom
  def isEmpty: Boolean =
    this match {
      case Cons(_, _) ⇒ false
      case Empty      ⇒ true
    }

  // returnerer størrelsen på listen
  def size: Int =
    this match {
      case Cons(_, tail) ⇒ 1 + tail.size
      case Empty         ⇒ 0
    }

  // henter første elementet i listen (kaster NoSuchElementException ved tom liste)
  def head: A =
    this match {
      case Cons(a, _) ⇒ a
      case Empty ⇒ throw new NoSuchElementException
    }

  // henter halen til listen (kaster NoSuchElementException ved tom liste)
  def tail: Lst[A] =
    this match {
      case Cons(_, tail) ⇒ tail
      case Empty ⇒ throw new NoSuchElementException
    }

  // finnes elementet med gitt index
  def isDefinedAt(x: Int): Boolean =
    this match {
      case Cons(_, tail) if x == 0 ⇒ true
      case Cons(_, tail)           ⇒ tail.isDefinedAt(x - 1)
      case Empty ⇒ false
    }


  // hent elementet med gitt index eller kast exception
  def apply(x: Int): A =
    this match {
      case Cons(a, _) if x == 0 ⇒ a
      case Cons(_, tail)        ⇒ tail.apply(x - 1)
      case Empty                ⇒ throw new NoSuchElementException
    }

  // returner en ny liste ved å kalle funksjonen for hvert element i lista
  def map[B](f: A => B): Lst[B] =
    this match {
      case Cons(a, tail) ⇒ Cons(f(a), tail.map(f))
      case Empty         ⇒ Empty
    }

  // legg "other" til på slutten av denne lista
  def append[AA >: A](other: Lst[AA]): Lst[AA] =
    this match {
      case Cons(a, tail) ⇒ Cons(a, tail.append(other))
      case Empty         ⇒ other
    }

  // returnerer en ny liste vel å kalle funksjonen f for alle elementene og appende resultatene etter hverandre
  // f.eks Cons(1, Cons(2, Nil)).flatMap(a => Cons(a, Cons(a + 1, Empty))) == Cons(1, Cons(2, Cons(2, Cons(3, Nil))))
  def flatMap[B](f: A => Lst[B]): Lst[B] =
    this match {
      case Cons(a, tail) ⇒ f(a).append(tail.flatMap(f))
      case Empty         ⇒ Empty
    }

  // returner en liste som inneholder all elementer som er 'true' for predikatet f
  def filter(f: A => Boolean): Lst[A] =
    this match {
      case Cons(a, tail) if f(a) ⇒ Cons(a, tail.filter(f))
      case Cons(a, tail)         ⇒ tail.filter(f)
      case Empty                 ⇒ Empty
    }

  // returnerer listen reversert
  def reverse: Lst[A] =
    this match {
      case Cons(a, tail) ⇒ tail.reverse.append(Cons(a, Empty))
      case Empty         ⇒ Empty
    }

  // Cons(1, Cons(2, Cons(3, Nil)).foldLeft(10)(f)
  // f(f(f(10, 1), 2), 3)
  // http://upload.wikimedia.org/wikipedia/commons/5/5a/Left-fold-transformation.png
  // @annotation.tailrec
  final def foldLeft[B](acc: B)(f: (B, A) => B): B =
    this match {
      case Cons(a, tail) ⇒ tail.foldLeft(f(acc, a))(f)
      case Empty         ⇒ acc
    }

  // Cons(1, Cons(2, Cons(3, Nil))).foldRight(10)(f)
  // f(3, f(2, f(3, 10)))
  // http://upload.wikimedia.org/wikipedia/commons/3/3e/Right-fold-transformation.png
  final def foldRight[B](acc: B)(f: (A, B) => B): B =
    this match {
      case Cons(a, tail) ⇒ f(a, tail.foldRight(acc)(f))
      case Empty         ⇒ acc
    }

  // returnerer en liste flatet ut (om det er mulig, ellers compile error)
  // f.eks. Cons(Cons(1, Nil), Cons(2, Nil)).flatten == Cons(1, Cons(2, Nil))
  def flatten[B](implicit f: A => Lst[B]): Lst[B] =
    this match {
      case Cons(a, tail) ⇒ f(a).append(tail.flatten)
      case Empty         ⇒ Empty
    }


  // returnerer summen av elementene i listen (om den inneholder nummer, ellers compile error)
  def sum[B >: A](implicit num: Numeric[B]): B =
    foldLeft(num.zero)(num.plus)
}

final case class Cons[A](x: A, xs: Lst[A]) extends Lst[A]

case object Empty extends Lst[Nothing]

object Lst {
  def apply[A](a: A*): Lst[A] =
    a.foldRight[Lst[A]](Empty){
      case (a, acc) ⇒ Cons(a, acc)
    }
}
