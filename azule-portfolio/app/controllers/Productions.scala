package controllers

import module.PortfolioModule.IMAGE_PATH
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.ControllerComponents
import service.ProductionService
import service.ProductionService._

import java.nio.file.Paths
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class Productions @Inject()(
  authAction: AuthAction,
  val productionService: ProductionService
)(implicit val cc: ControllerComponents) extends Controller {

  private def productionForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "alt" -> seq(text)
    )(ProductionInfoData.apply)(ProductionInfoData.unapply)
  )

  def index() = authAction { implicit request =>
    Ok(views.html.page.manageProduction(productionService.load))
  }

  def add() = authAction { implicit request =>
    Ok(views.html.page.production.productionForm(productionForm, routes.Images.upload()))
  }

  def show(id: Long) = authAction { implicit request =>
    Ok(views.html.page.production.show(productionService.load.find(_.id == id).get))
  }

  def create() = authAction(parse.multipartFormData) { implicit request =>
    productionForm.bindFromRequest().fold(
      formWithErrors => {
        println(formWithErrors)
        BadRequest(views.html.production.form.productionForm(formWithErrors))
      },
      data => {
        println(request.body.file("thumbnail_image"))
        println("=============")
        request.body.file("thumbnail_image").map { pic =>
          pic.ref.copyTo(Paths.get(s"$IMAGE_PATH/${data.title}_thumbnail.png"), replace = true).toString
        }.fold {
          BadRequest(views.html.production.form.productionForm(productionForm.fill(data))) // need thumbnail image
        } { thumbnailPath =>
          Ok(views.html.page.production.show(productionService.create(
            data.toProductionData(thumbnailPath, Nil, Nil)
          )))
        }
      }
    )
  }

  def sendFile(filename: String) = authAction { implicit request =>
    implicit val ec: ExecutionContext = cc.executionContext
    Ok.sendFile(
      content = new java.io.File(filename),
      inline = true
    )
  }

}
