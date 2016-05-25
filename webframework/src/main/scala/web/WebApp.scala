package web

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{Servlet, ServletConfig, ServletRequest, ServletResponse}

import scala.xml.NodeSeq

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
