package controllers

import models.PageSupport.ContentType
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.ControllerComponents
import service.PageService

import javax.inject.Inject

class Pages @Inject()(
  authAction: AuthAction,
  val pageService: PageService
)(implicit val cc: ControllerComponents) extends Controller {

  private def pageForm = Form(
    tuple(
      "contentType" -> nonEmptyText,
      "content" -> nonEmptyText
    )
  )

  private def windowId(ct: String): String = s"window-edit-page-$ct"

  def index() = authAction { implicit request =>
    Ok(views.html.page.managePage())
  }

  def edit(contentType: String) = authAction { implicit request =>
    pageService.load(ContentType.find(contentType)) match {
      case page if page.contentType == ContentType.Dummy =>
        BadRequest
      case page =>
        Ok(views.html.page.page.pageForm(contentType, windowId(contentType), pageForm.fill((page.contentType.toString, page.content))))
    }
  }

  def update() = authAction { implicit request =>
    pageForm.bindFromRequest().fold(
      formWithError => {
        val contentType = formWithError("contentType").value.getOrElse(ContentType.Dummy).toString
        BadRequest(views.html.page.page.pageForm(contentType, windowId(contentType), formWithError))
      },
      data => {
        pageService.load(ContentType.find(data._1)) match {
          case page if page.contentType == ContentType.Dummy =>
            BadRequest(views.html.page.managePage())
          case page =>
            Ok(views.html.page.page.show(pageService.update(page.contentType, data._2)))
        }
      }
    )
  }

}
