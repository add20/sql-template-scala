package util

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.xml.XML
import scala.collection.mutable
import java.io.InputStream

object SqlTemplateExecutor:

  case class SqlQuery(name: String, raw: String, paramOrder: List[String], isUpdate: Boolean)

  private val queries: mutable.Map[String, SqlQuery] = mutable.Map()

  // 初期化：XMLテンプレート読み込み（複数ファイル対応可）
  def loadXml(fileName: String): Unit =
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(fileName)
    val xml = XML.load(is)
    for sql <- xml \\ "sql" do
      val name = (sql \@ "name").trim
      val raw = sql.text.trim
      val (parsedSql, params) = parseSqlTemplate(raw)
      val isUpdate = name.endsWith("!")
      queries(name) = SqlQuery(name, parsedSql, params, isUpdate)

  // SQLテンプレートから :param を ? に変換
  private def parseSqlTemplate(raw: String): (String, List[String]) =
    val regex = """:(\w+)""".r
    val paramOrder = regex.findAllMatchIn(raw).map(_.group(1)).toList
    val replaced = regex.replaceAllIn(raw, "?")
    (replaced, paramOrder)

  def execute[T]
    (name: String, params: Map[String, Any])
    (updateMapper: Int => T)
    (resultSetMapper: ResultSet => T)
    (using conn: Connection): T =

    queries.get(name) match
      case Some(query) =>
        val stmt = conn.prepareStatement(query.raw)

        try
          for ((paramName, idx) <- query.paramOrder.zipWithIndex) do
            stmt.setObject(idx + 1, params(paramName))

          if query.isUpdate then
            val updatedRows = stmt.executeUpdate()
            updateMapper(updatedRows)
          else
            val rs = stmt.executeQuery()

            try
              resultSetMapper(rs)
            finally
              rs.close
        finally
          stmt.close

      case None =>
        throw new IllegalArgumentException(s"Query not found: $name")

  def executeUpdate
    (name: String, params: Map[String, Any])
    (using conn: Connection): Int =

      execute
        (name, params)
        (identity)
        (_ => throw new IllegalArgumentException("This is executeUpdate not executeQuery method."))

  def executeQuery[T]
    (name: String, params: Map[String, Any])
    (resultSetMapper: ResultSet => T)
    (using conn: Connection): T =

    execute
      (name, params)
      (_ => throw new IllegalArgumentException("This is executeQuery not executeUpdate method."))
      (resultSetMapper)
