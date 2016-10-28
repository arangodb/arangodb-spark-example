package com.arangodb

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.arangodb.spark.ArangoSpark
import com.arangodb.spark.ArangoSpark
import scala.beans.BeanProperty

object Server {

  class Handler(val sc: SparkContext) extends AbstractHandler {
    var html = <h1>Something, innit</h1>

    override def handle(target: String,
                        req: Request,
                        httpReq: HttpServletRequest,
                        httpRes: HttpServletResponse) = {
      httpRes.setContentType("text/html")
      httpRes.setStatus(HttpServletResponse.SC_OK)
      getMovies(sc).foreach { x => httpRes.getWriter().println(x.title + "<br />") }
      req.setHandled(true)
    }
  }

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf(false)
      .setMaster("local") //tcp://arangodb-proxy.marathon.mesos:8529
      .setAppName("test")
      .set("arangodb.host", "192.168.173.86") //arangodb-proxy.marathon.mesos
      .set("arangodb.port", "8530")

    val sc = new SparkContext(conf)

    val server = new org.eclipse.jetty.server.Server(8080)
    server.setHandler(new Handler(sc))
    server.start
  }

  def getMovies(sc: SparkContext): Array[Movie] = {
    val rdd = ArangoSpark.load[Movie](sc, "Movie")
    val rdd2 = rdd.filter { x => x.title.matches(".*Lord.*Rings.*") }
    rdd2.collect()
  }

  case class Movie(@BeanProperty title: String) {
    def this() = this(title = null)
  }

}