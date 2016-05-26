package web

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{Servlet, ServletConfig, ServletRequest, ServletResponse}

import scala.xml.NodeSeq
import scala.collection.JavaConverters._

object Response {
  def apply(f: HttpServletResponse => Unit): HttpServletResponse => HttpServletResponse = {
    in =>
      f(in)
      in
  }
}

object Status {
  val OK = Response(_.setStatus(200))
  val NOT_FOUND = Response(_.sendError(404))
}

object Html {
  def apply(html: NodeSeq): (HttpServletResponse) ⇒ HttpServletResponse =
    Response(_.getWriter.write("<!DOCTYPE html>\n" + html.toString()))
}

object Path {
  def unapply(req: HttpServletRequest): Option[String] =
    Some(req.getRequestURI)
}

object Method {
  def unapply(req: HttpServletRequest): Option[String] =
    Some(req.getMethod)
}

object GET {
  def unapply(req: HttpServletRequest): Boolean =
    req.getMethod == "GET"
}

object POST {
  def unapply(req: HttpServletRequest): Boolean =
    req.getMethod == "POST"
}

object Params {
  def unapply(req: HttpServletRequest): Option[Map[String, Seq[String]]] =
    Some(req.getParameterMap.asScala.toMap.mapValues(_.toSeq))
}

object Headers {
  def unapply(req: HttpServletRequest): Option[Map[String, Seq[String]]] =
    Some(
      req.getHeaderNames.asScala.map(
        name ⇒ (name, req.getHeaders(name).asScala.toSeq)
      ).toMap
    )
}

object Parts {
  def unapplySeq(s: String): Option[Seq[String]] =
    Some(s.split('/')).map(_.filterNot(_.isEmpty))
}

trait WebApp extends Servlet {

  private var _config: ServletConfig = _

  def init(config: ServletConfig) {
    _config = config
  }

  def getServletConfig: ServletConfig =
    _config

  def getServletInfo: String =
    "WebApp"

  def destroy(): Unit = {}

  def service(req: ServletRequest, res: ServletResponse): Unit = {
    (req, res) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        handle.lift(hreq).getOrElse(Status.NOT_FOUND)(hres)
    }
  }

  def handle: PartialFunction[HttpServletRequest, HttpServletResponse => HttpServletResponse]
}
