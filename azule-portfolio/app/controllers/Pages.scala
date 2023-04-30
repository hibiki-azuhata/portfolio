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

  def index() = authAction { implicit request =>
    Ok()
  }

  def edit(contentType: String) = authAction { implicit request =>
    pageService.load(ContentType.find(contentType)) match {
      case page if page.contentType == ContentType.Dummy =>
        BadRequest
      case page =>
        Ok()
    }
  }

  def update() = authAction { implicit request =>
    pageService.load(ContentType.find(contentType)) match {
      case page if page.contentType == ContentType.Dummy =>
        BadRequest
      case page =>
        Ok()
    }
  }

}
