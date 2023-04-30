package controllers

import javax.inject._
import play.api.mvc._
import service.ProductionService

class IntroductionController @Inject()(
  val productionService: ProductionService,
  implicit val cc: ControllerComponents
) extends Controller {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def manual() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.page.manual())
  }

  def work() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.page.work(productionService.load))
  }

  def about() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.page.about())
  }
}
