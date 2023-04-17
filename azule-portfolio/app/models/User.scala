package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class User(
  id: Long,
  name: String,
  password: String
)

object User extends SkinnyCRUDMapper[User] {
  override lazy val tableName = "users"
  override lazy val defaultAlias = createAlias("u")

  override def extract(rs: WrappedResultSet, n: ResultName[User]): User = new User(
    id = rs.get(n.id),
    name = rs.get(n.name),
    password = rs.get(n.password)
  )

  def create(name: String, password: String): User = {
    val id = User.createWithNamedValues(
      column.name -> name,
      column.password -> password
    )
    User(id, name, password)
  }

  def getUser(name: String, password: String): Option[User] =
    User.findBy(sqls.eq(column.name, name).and.eq(column.password, password))

}
