package models

import akka.stream.scaladsl.FileIO
import play.api.http.HttpEntity
import play.api.mvc.{ResponseHeader, Result}
import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

import java.nio.file.{Files, Paths}

case class Production(
  id: Long,
  title: String,
  content: String,
  thumbnail: String,
  images: Seq[Image] = Nil,
  tags: Seq[Tag] = Nil
) {
  def src = {
    FileIO.fromPath(
      Paths.get(thumbnail)
    )

  }
}

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
    imageIds: Seq[Long],
    tagIds: Seq[Long]
  ): Production = {
    val pId = Production.createWithNamedValues(
      column.title -> title,
      column.content -> content,
      column.thumbnail -> thumbnail
    )
    ProductionImage.create(pId, imageIds)
    ProductionTag.create(pId, tagIds)
    val imageObjects = if(imageIds.isEmpty) Nil else Image.findAllBy(sqls.in(Image.column.id, imageIds))
    val tagObjects = if(tagIds.isEmpty) Nil else Tag.findAllBy(sqls.in(Tag.column.id, tagIds))
    Production(pId, title, content, thumbnail, imageObjects, tagObjects)
  }

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