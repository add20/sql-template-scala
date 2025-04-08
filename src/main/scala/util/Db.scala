package util

import java.sql.{Connection, DriverManager}
import java.util.Properties
import scala.util.Using

object Db:

  private val props = new Properties()
  props.load(getClass.getClassLoader.getResourceAsStream("db.properties"))

  private val url = props.getProperty("db.url")
  private val user = props.getProperty("db.user")
  private val password = props.getProperty("db.password")

  def getConnection(): Connection =
    DriverManager.getConnection(url, user, password)

  // トランザクションを安全に実行し、コネクションを implicit に渡す
  def withTransaction[T](block: Connection ?=> T): T =
    val conn = getConnection()
    try
      conn.setAutoCommit(false)
      val result = block(using conn)
      conn.commit()
      result
    catch
      case e: Exception =>
        conn.rollback()
        throw e
    finally
      conn.close()