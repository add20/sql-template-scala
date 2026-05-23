package util

import java.sql.ResultSet

enum SqlResult:
  case QueryResult(rs: ResultSet)
  case UpdateResult(updatedRows: Int)

  def getQueryResult: ResultSet =
    this match
      case QueryResult(rs) => rs
      case UpdateResult(_) => throw RuntimeException("unexpected query result")
    
  def getUpdateResult: Int =
    this match
      case QueryResult(_) => throw RuntimeException("unexpected query result")
      case UpdateResult(rows) => rows
