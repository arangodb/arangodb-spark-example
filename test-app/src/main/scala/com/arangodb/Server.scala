package com.arangodb

import scala.beans.BeanProperty

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request
import com.arangodb.util.MapBuilder
import scala.reflect.ClassTag

object Server {

  private val COLLECTION_NAME = "ring_movies"

  class Handler(arangoDB: ArangoDB) extends AbstractHandler {
    override def handle(target: String,
                        req: Request,
                        httpReq: HttpServletRequest,
                        httpRes: HttpServletResponse) = {
      httpRes.setContentType("text/html")
      httpRes.setStatus(HttpServletResponse.SC_OK)
      httpRes.getWriter().println("<h1>all movies that have \"Lord.*Rings\" in their title</h1>")
      val cursor = getMovies(arangoDB)
      while (cursor.hasNext()) {
        httpRes.getWriter().println(cursor.next().title + "<br />")
      }
      req.setHandled(true)
    }
  }

  def main(args: Array[String]): Unit = {
    val arangoDB = new ArangoDB.Builder().host("arangodb-proxy.marathon.mesos").port(8529).user("root").build();
    val server = new org.eclipse.jetty.server.Server(8080)
    server.setHandler(new Handler(arangoDB))
    server.start
  }

  def getMovies(arangoDB: ArangoDB): ArangoCursor[Movie] = {
    arangoDB.db().query("return doc in @@col return doc", new MapBuilder().put("@col", COLLECTION_NAME).get(), null, classOf[Movie])
  }

  case class Movie(@BeanProperty title: String) {
    def this() = this(title = null)
  }

}