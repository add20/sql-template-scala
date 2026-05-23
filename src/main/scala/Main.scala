import java.sql.{Connection, ResultSet}

import java.util.UUID

import model.User
import util.Db
import util.SqlTemplateExecutor

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
  SqlTemplateExecutor.loadXml("sql/queries.sql.xml")
  Db.withTransaction {
    val conn = summon[Connection]

    // val id = User.insertUser("test4@example.com", "password", Some("テストユーザー4"))
    // println(s"test user id is $id")

    val user = User.selectUserByEmail("test4@example.com")
    println(user)

    User.updateUserScreenName(
      UUID.fromString("785ced97-6f79-4cb1-8e89-b8469516cb04")
    , "NEW テストユーザー3"
    )
  }