package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class Production(
  id: Long,
  title: String,
  content: String,
  thumbnail: String,
  images: Seq[Image] = Nil
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

  def create(
    title: String,
    content: String,
    thumbnail: String,
    imageIds: Seq[Long]
  ): Production = {
    val pId = Production.createWithNamedValues(
      column.title -> title,
      column.content -> content,
      column.thumbnail -> thumbnail
    )
    ProductionImage.create(pId, imageIds)
    val imageObjects = if(imageIds.isEmpty) Nil else Image.findAllBy(sqls.in(Image.column.id, imageIds))
    Production(pId, title, content, thumbnail, imageObjects)
  }

  hasManyThrough[Image](
    through = ProductionImage,
    many = Image,
    merge = (p, images) => p.copy(images = images)
  ).byDefault
}