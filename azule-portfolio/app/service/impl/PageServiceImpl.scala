package service.impl

import models.Page
import models.PageSupport.ContentType
import scalikejdbc._
import service.PageService

class PageServiceImpl extends PageService {

  def update(contentType: ContentType, content: String): Page = {
    val column = Page.column
    val page = load(contentType)
    Page.updateById(page.id).withNamedValues(
      column.contentType -> contentType.toString,
      column.content -> content
    )
    page.copy(contentType = contentType, content = content)
  }

  def load(contentType: ContentType): Page = {
    val column = Page.column
    Page.findBy(sqls.eq(column.contentType, contentType.toString)).getOrElse {
      Page.setUp
      println(s"fatal error: page not found $contentType")
      Page.findBy(sqls.eq(column.contentType, contentType.toString)).getOrElse(Page.dummy)
    }
  }
}
