package buzzwords

import java.io.File
import scala.io.Source

/**
 * Det er fint å bruke typer som dokumentasjon/sikkerhet, og det gjør vi her
 *  med disse wrapper-typene.
 *
 * `extends AnyVal` vil si at det er er value types - dvs at det
 *  ikke er noe runtime-overhead - de compilerer til rene `String`er
 */

case class Line(value: String) extends AnyVal
case class Word(value: String) extends AnyVal

object Buzzwords extends App {

  def wordsFromFiles(files: File*): Seq[Word] = {
    //nøstede funksjoner, for å utrykke at vi ikke vil bruke de utenfor `wordsFromFiles`
    def lines(file: File): Seq[Line] =
      Source.fromFile(file, "UTF-8")
        .getLines() // returnerer en Iterator[String]
        .toSeq      // konsumér hele iteratoren og samle i en `Seq` (felles supertype for
                    // blandt annet `List` og `Array` - mye brukt)
        .map(Line)  // `Line` er en case class, så vi trenger ikke si `new`

    def words(line: Line): Seq[Word] =
      line.value
        .toLowerCase
        .split("[,\\. \\-]")  // `split` er en av de rare java-metodene som tar en
                              // regex-string, så vi utnytter det
        .filterNot(_.isEmpty) // måten vi splitter på gjør at vi får mange tomme strenger
        .map(Word)

    /**
     *  Dette er et fint eksempel på å lage små ting og sette de sammen,
     *   som vi gjerne streber etter med funksjonell programmering
     *
     *  Legg spesielt merke til at gitt en `Seq[File]`, en `File => Seq[Line]` ,
     *   og en `Line => Seq[Word]` er det nesten umulig å implementere resten feil.
     *
     *  De to alternativene under gjør akkurat det samme - forskjellen er
     *  mest stil - hvorvidt det gir verdi å navngi tingene underveis
     */

    val alternativ1: Seq[Word] =
      files.flatMap(lines).flatMap(words) // skrives gjerne `files flatMap lines flatMap words`

    val alternativ2: Seq[Word] =
      for {
        file ← files
        line ← lines(file)
        word ← words(line)
      } yield word

    alternativ2
  }

  val buzzwords: Seq[Word]  =
    wordsFromFiles(new File("buzz.txt"))

  val stopWords: Set[Word] = //`Set` fordi rekkefølge ikke har noe å si,
                             // og vi vil ikke ha duplikater
    wordsFromFiles(new File("stopp-norsk.txt"), new File("stop-english.txt")).toSet

  val filteredBuzzwords: Seq[Word] =
    /**
     * Et `Set` er også en funksjon - `trait Set[A] extends (A => Boolean) with ...`,
     *  som sjekker om en `A` er medlem av settet - altså `contains`.
     *
     * Når vi gjør `filterNot` kan vi derfor bare gi `Set`et direkte.
     *
     * `buzzwords.filterNot(word => stopWords.contains(word))` hadde blitt det samme
     */
    buzzwords filterNot stopWords

  val mostPopular: Seq[(Word, Int)] =
    filteredBuzzwords
    .groupBy(identity)  // gruppér på ordet selv - returnerer `Map[Word, Seq[Word]]`
    .mapValues(_.size)  // teller opp forekomster av words
    .toSeq              // tilbake til `Seq`, fordi det ikke gir mening å sortere et `Map`
    .sortBy{            // pattern matching for å hente ut `count` fra tuppelet
      case (word, count) ⇒ -count //`-` fordi vi vil sortere descending
    }
    .take(5)            // henter ut de 5 mest populære


  def padString(s: String, n: Int): String =
    s + (" " * (n - s.length)) //`String` i Scala har en `*`-metode for å repetere seg selv n ganger

  mostPopular foreach {
    case (word, count) ⇒
      println(s"${padString(word.value, 20)}: ${"*" * count}")
  }
}