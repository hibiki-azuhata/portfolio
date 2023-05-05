package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.ControllerComponents
import service.ProductionService
import service.ProductionService._

import javax.inject.Inject

class Productions @Inject()(
  authAction: AuthAction,
  val productionService: ProductionService
)(implicit val cc: ControllerComponents) extends Controller {

  private def productionForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "thumbnail" -> default(text, "")
    )(ProductionInfoData.apply)(ProductionInfoData.unapply)
  )

  def index() = authAction { implicit request =>
    Ok(views.html.page.manageProduction(productionService.load))
  }

  def add() = authAction { implicit request =>
    Ok(views.html.page.production.productionForm(Messages("manage.production.create"), "window-new-production", productionForm, routes.Productions.create()))
  }

  def edit(id: Long) = authAction { implicit request =>
    productionService.find(id).fold(
      BadRequest(views.html.page.production.productionForm(Messages("manage.production.create"), "window-new-production", productionForm, routes.Productions.create()))
    )( production =>
      Ok(views.html.page.production.productionForm(
        Messages("manage.production.create"), s"window-edit-production-$id",
        productionForm.fill(ProductionInfoData.toProductionData(production)),
        routes.Productions.update()
      ))
    )
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
        Images.upload("thumbnail_image", s"${data.title}_").fold {
          BadRequest(views.html.production.form.productionForm(productionForm.fill(data))) // need thumbnail image
        } { thumbnailPath =>
          Ok(views.html.page.production.show(productionService.create(
            data.toProductionData(Images.getName(thumbnailPath), Nil)
          )))
        }
      }
    )
  }

  def update() = authAction(parse.multipartFormData) { implicit request =>
    productionForm.bindFromRequest().fold(
      formWithErrors => {
        BadRequest(views.html.production.form.productionForm(formWithErrors))
      },
      data => {
        val newThumbnail = Images.upload("thumbnail_image", s"${data.title}_")
        productionService.update(data.toProductionData(newThumbnail.map(Images.getName).getOrElse(data.thumbnail), Nil)).fold {
          NotFound(Messages("production.update.notFound"))
        } { production =>
          Ok(views.html.page.production.show(production))
        }
      }
    )
  }
}
