package controllers

import module.PortfolioModule.IMAGE_PATH
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.ControllerComponents
import service.ProductionService
import service.ProductionService._

import java.nio.file.Paths
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class Productions @Inject()(
  authAction: AuthAction,
  val productionService: ProductionService
)(implicit val cc: ControllerComponents) extends Controller with I18nSupport {

  private def productionForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "alt" -> seq(text)
    )(ProductionInfoData.apply)(ProductionInfoData.unapply)
  )

  def index() = authAction { implicit request =>
    Ok(views.html.index())
  }

  def newProduction() = authAction { implicit request =>
    Ok(views.html.production.form.productionForm(productionForm))
  }

  def show(id: Long) = authAction { implicit request =>
    Ok(views.html.production.show(productionService.load.find(_.id == id).get))
  }

  def create() = authAction(parse.multipartFormData) { implicit request =>
    productionForm.bindFromRequest().fold(
      formWithErrors => {
        println(formWithErrors)
        BadRequest(views.html.production.form.productionForm(formWithErrors))
      },
      data => {
        val thumbnailPath = request.body.file("thumbnail_image").map { pic =>
          pic.ref.copyTo(Paths.get(s"$IMAGE_PATH/${data.title}_thumbnail"), replace = true).toString
        }.get
        val imagePaths = (0 until 4).flatMap { i =>
          request.body.file(s"image_$i").map { image =>
            image.ref.copyTo(Paths.get(s"$IMAGE_PATH/${data.title}_image$i"), replace = true).toString
          }
        }
        val id = productionService.create(
          data.toProductionData(thumbnailPath, data.alt.zip(imagePaths).map(j => ImageData(j._2, j._1)), Nil)
        ).id
        Redirect(routes.Productions.show(id))
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
