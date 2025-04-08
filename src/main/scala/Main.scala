import java.sql.{Connection, ResultSet}

import util.Db

import java.util.UUID

def insertUser(email: String, password: String, screenName: Option[String])(using conn: Connection): UUID =
  val sql = "INSERT INTO users (users_id, users_email, users_password, users_screen_name) VALUES (?, ?, ?, ?)"
  val id = UUID.randomUUID()
  val stmt = conn.prepareStatement(sql)
  stmt.setObject(1, id)
  stmt.setString(2, email)
  stmt.setString(3, password)
  stmt.setString(4, screenName.orNull)
  stmt.executeUpdate()
  stmt.close()
  id

@main def runApp(): Unit =
  Db.withTransaction {
    summon[Connection]
    val id = insertUser("test3@example.com", "password", Some("テストユーザー3"))
    println(s"test user id is $id")
  }