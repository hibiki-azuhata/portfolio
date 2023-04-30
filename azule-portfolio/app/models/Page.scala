package models

import models.PageSupport.ContentType
import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper


case class Page(
  id: Long,
  contentType: ContentType,
  content: String
)

object Page extends SkinnyCRUDMapper[Page] {
  override lazy val tableName = "pages"
  override lazy val defaultAlias = createAlias("pg")

  override def extract(rs: WrappedResultSet, n: ResultName[Page]): Page = new Page(
    id = rs.get(n.id),
    contentType = ContentType.find(rs.get(n.contentType)),
    content = rs.get(n.content)
  )

  def setUp: Unit = {
    val pages = Page.findAll()
    if (pages.map(_.contentType == ContentType.Manual).isEmpty) {
      Page.createWithNamedValues(
        column.contentType -> ContentType.Manual.toString,
        column.content -> "<h1> this is manual page </h1>"
      )
    }
    if (pages.map(_.contentType == ContentType.About).isEmpty) {
      Page.createWithNamedValues(
        column.contentType -> ContentType.About.toString,
        column.content -> "<h1> this is about me page </h1>"
      )
    }
  }

  def dummy: Page = Page(-1, ContentType.Dummy, "page not found")
}
object PageSupport {

  sealed trait ContentType
  object ContentType {
    case object Manual extends ContentType
    case object About extends ContentType

    case object Dummy extends ContentType

    private def values: Seq[ContentType] = Seq(Manual, About)

    def find(value: String): ContentType =
      values.find(_.toString == value).getOrElse(Dummy)
  }
}
