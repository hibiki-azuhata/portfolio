package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class Image(
  id: Long,
  url: String,
  alt: String
)

object Image extends SkinnyCRUDMapper[Image] {
  override lazy val tableName = "images"
  override lazy val defaultAlias = createAlias("i")
  override def extract(rs: WrappedResultSet, n: ResultName[Image]): Image = new Image(
    id = rs.get(n.id),
    url = rs.get(n.url),
    alt = rs.get(n.alt)
  )
}
