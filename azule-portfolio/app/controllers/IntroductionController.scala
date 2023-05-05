package controllers

import models.PageSupport.ContentType

import javax.inject._
import play.api.mvc._
import service.{PageService, ProductionService}

class IntroductionController @Inject()(
  val productionService: ProductionService,
  val pageService: PageService,
  implicit val cc: ControllerComponents
) extends Controller {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(views.html.page.page.show(pageService.load(ContentType.Manual))))
  }

  def manual() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.page.page.show(pageService.load(ContentType.Manual)))
  }

  def work() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.page.work(productionService.load))
  }

  def about() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.page.page.show(pageService.load(ContentType.About)))
  }
}
