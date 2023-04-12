package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{BaseController, ControllerComponents}
import service.ProductionService
import service.ProductionService.ProductionData

import java.nio.file.Paths
import javax.inject.Inject

class Productions @Inject()(
  val controllerComponents: ControllerComponents,
  val productionService: ProductionService
) extends BaseController with I18nSupport {

  private def productionForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "thumbnail" -> nonEmptyText,
      "imageIds" -> seq(longNumber),
      "tagIds" -> seq(longNumber)
    )(ProductionData.apply)(ProductionData.unapply)
  )
  def index() = Action { implicit request =>
    Ok(views.html.index())
  }

  def newProduction() = Action { implicit request =>
    Ok(views.html.production.form.productionForm(productionForm))
  }

  def show(id: Long) = Action { implicit request =>
    Ok(views.html.production.show(productionService.load.find(_.id == id).get))
  }

  def create() = Action(parse.multipartFormData) { implicit request =>
    productionForm.bindFromRequest().fold(
      formWithErrors => {
        Redirect(routes.HomeController.index())
      },
      data => {
        val path = request.body.file("picture").map { pic =>
          val name = Paths.get(pic.filename).getFileName
          pic.ref.copyTo(Paths.get(s"data/$name"), replace = true)
        }.get
        println(path.toAbsolutePath.toString)
        val id = productionService.create(data.copy(thumbnail = s"${path.toAbsolutePath.toString}")).id
        Redirect(routes.Productions.show(id))
      }
    )
  }

}
