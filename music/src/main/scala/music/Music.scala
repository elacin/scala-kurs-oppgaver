package music

import scala.xml._

object Music {

  /*
  http://lyrics.wikia.com/api.php?func=getArtist&fmt=xml&fixXML&artist={artist}
   */
  def searchArtist(artist: String): Seq[Artist] = ???

  /*
   http://lyrics.wikia.com/api.php?fmt=xml&artist={artist}&song={song}
   */
  def findSong(artist: String, song: String): Seq[Song] = ???

  /*
  i retur url fra "findSong" ligger den en url til full html versjon av lyrics for sangen
  Benytt TagSoup støtten i Dispath til å hente ut lyrics fra denne html siden
   */
  def findLyrics(page: String): String = ???

  /*
  skal benyttes for parsing av "searchArtist"
   */
  def parseSearchArtistResponse(xml: Elem): Seq[Artist] = ???

  /*
  scala benyttes for parsing av "findSong"
   */
  def parseFindSongResponse(xml: Elem): Seq[Song] = ???
}

case class Artist(name: String, albums: Seq[Album])
case class Album(name: String, year: String, amazonLink: String, songs: Seq[String])
case class Song(artist: String, title: String, lyrics: String, url: String)
