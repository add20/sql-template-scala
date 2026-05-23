package util

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.xml.XML
import scala.collection.mutable
import java.io.InputStream

import util.SqlResult

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

  def execute(
    name: String,
    params: Map[String, Any]
  )(using conn: Connection): SqlResult =

    queries.get(name) match
      case Some(query) =>
        val stmt = conn.prepareStatement(query.raw)

        for ((paramName, idx) <- query.paramOrder.zipWithIndex) do
          stmt.setObject(idx + 1, params(paramName))

        if query.isUpdate then
          val updatedRows = stmt.executeUpdate()
          stmt.close()
          SqlResult.UpdateResult(updatedRows)
        else
          SqlResult.QueryResult(stmt.executeQuery())

      case None =>
        throw new IllegalArgumentException(s"Query not found: $name")
