package model

import java.sql.{Connection, Timestamp}
import java.util.UUID

import util.SqlResult
import util.SqlTemplateExecutor

case class User(
  id: UUID,
  email: String,
  password: String,
  screenName: Option[String],
  createdAt: Timestamp,
  updatedAt: Timestamp
)

object User:
  def insertUser(email: String, password: String, screenName: Option[String])(using conn: Connection): UUID =
    val id = UUID.randomUUID()
    SqlTemplateExecutor.execute("insert-user!", Map(
      "users_id" -> id,
      "users_email" -> email,
      "users_password" -> password,
      "users_screen_name" -> screenName.orNull
    ))
    id

  def selectUserByEmail(email: String)(using conn: Connection): Option[User] =
    val sqlResult = SqlTemplateExecutor.execute("select-user-by-email", Map("users_email" -> email))
    val rs = sqlResult.getQueryResult
    Option.when(rs.next)(User(
        UUID.fromString(rs.getString("users_id")),
        rs.getString("users_email"),
        rs.getString("users_password"),
        Option(rs.getString("users_screen_name")),
        rs.getTimestamp("users_created_at"),
        rs.getTimestamp("users_updated_at")
      ))

  def updateUserScreenName(id: UUID, newName: String)(using conn: Connection): Boolean =
    val sqlResult = SqlTemplateExecutor.execute("update-user-screen-name!", Map(
      "users_id" -> id,
      "users_screen_name" -> newName
    ))
    val rows = sqlResult.getUpdateResult
    rows >= 1
