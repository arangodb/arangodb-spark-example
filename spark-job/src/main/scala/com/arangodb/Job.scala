package com.arangodb

import scala.beans.BeanProperty

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

import com.arangodb.spark.ArangoSpark

object Job {

  private val COLLECTION_NAME = "ring_movies"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf(false)
      .setMaster("mesos://master.mesos:5050")
      .setAppName("movie-example")
      .set("arangodb.host", "arangodb-proxy.marathon.mesos")
      .set("arangodb.port", "8529")
      .set("arangodb.user", "root")

    setupDB(conf)
    val sc = new SparkContext(conf)

    val rdd = ArangoSpark.load[Movie](sc, "Movie")
    val rdd2 = rdd.filter { x => x.title.matches(".*Lord.*Rings.*") }
    ArangoSpark.save(rdd2, COLLECTION_NAME)
  }

  def setupDB(conf: SparkConf): Unit = {
    val arangoDB = new ArangoDB.Builder().host(conf.get("arangodb.host"),conf.get("arangodb.port").toInt).user(conf.get("arangodb.user")).build()
    try {
      arangoDB.db().collection(COLLECTION_NAME).drop()
    } catch {
      case e: ArangoDBException =>
    }
    arangoDB.db().createCollection(COLLECTION_NAME)
  }

  case class Movie(@BeanProperty title: String) {
    def this() = this(title = null)
  }

}