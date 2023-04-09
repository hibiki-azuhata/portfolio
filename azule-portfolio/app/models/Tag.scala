package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class Tag(id: Long, name: String)

object Tag extends SkinnyCRUDMapper[Tag] {
  override lazy val tableName = "tags"
  override lazy val defaultAlias = createAlias("t")

  override def extract(rs: WrappedResultSet, n: ResultName[Tag]): Tag = new Tag(
    id = rs.get(n.id),
    name = rs.get(n.name)
  )
}
