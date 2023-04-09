package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class Production(
  id: Long,
  title: String,
  content: String,
  thumbnail: String,
  images: Seq[Image] = Nil,
  tags: Seq[Tag] = Nil
)

object Production extends SkinnyCRUDMapper[Production] {
  override lazy val tableName = "productions"
  override lazy val defaultAlias = createAlias("p")
  override def extract(rs: WrappedResultSet, n: ResultName[Production]): Production = new Production(
    id = rs.get(n.id),
    title = rs.get(n.title),
    content = rs.get(n.content),
    thumbnail = rs.get(n.thumbnail)
  )

  hasManyThrough[Image](
    through = ProductionImage,
    many = Image,
    merge = (p, images) => p.copy(images = images)
  ).byDefault

  hasManyThrough[Tag](
    through = ProductionTag,
    many = Tag,
    merge = (p, tags) => p.copy(tags = tags)
  ).byDefault
}