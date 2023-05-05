package controllers

import play.api.data.{Form, FormError}
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
        routes.Productions.update(id)
      ))
    )
  }

  def show(id: Long) = authAction { implicit request =>
    productionService.find(id).fold {
      BadRequest(s"not found production Id $id")
    } { p =>
      Ok(views.html.page.production.show(p))
    }
  }

  def create() = authAction(parse.multipartFormData) { implicit request =>
    productionForm.bindFromRequest().fold(
      formWithErrors => {
        BadRequest(views.html.page.production.productionForm(
          Messages("manage.production.create"),
          "window-new-production",
          formWithErrors,
          routes.Productions.create())
        )
      },
      data => {
        Images.upload("thumbnail_image", s"${data.title}_").fold {
          val formData = productionForm.fill(data)
          BadRequest(views.html.page.production.productionForm(
            Messages("manage.production.create"),
            "window-new-production",
            formData.copy(errors = FormError("thumbnail", Messages("manage.production.error.notFound.thumbnail")) +: formData.errors),
            routes.Productions.create())
          )
        } { thumbnailPath =>
          Ok(views.html.page.production.show(productionService.create(
            data.toProductionData(Images.getName(thumbnailPath), Nil)
          )))
        }
      }
    )
  }

  def update(id: Long) = authAction(parse.multipartFormData) { implicit request =>
    productionForm.bindFromRequest().fold(
      formWithErrors => {
        BadRequest(views.html.page.production.productionForm(
          Messages("manage.production.create"), s"window-edit-production-$id",
          formWithErrors,
          routes.Productions.update(id)
        ))
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
