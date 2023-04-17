package controllers

import javax.inject._
import play.api.mvc._
import service.ProductionService

class IntroductionController @Inject()(
  val productionService: ProductionService,
  implicit val cc: ControllerComponents
) extends Controller {

  def index() = Action { implicit request: Request[AnyContent] =>
    println(productionService.load)
    Ok(views.html.index())
  }
}
