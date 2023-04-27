package controllers

import module.PortfolioModule.IMAGE_PATH
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.ControllerComponents
import service.ImageService

import java.nio.file.Paths
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class Images @Inject()(
  authAction: AuthAction,
  val imageService: ImageService
)(implicit val cc: ControllerComponents) extends Controller {

  private def imageForm = Form(
    "alt" -> nonEmptyText
  )
  def upload() = authAction(parse.multipartFormData) { implicit request =>
    println(request)
    imageForm.bindFromRequest().fold(
      _ => {
        println("this")
        BadRequest(Messages("image.upload.failed.noAlt"))
      },
      data => {
        request.body.file("image").map { image =>
          image.ref.copyTo(Paths.get(s"$IMAGE_PATH/${data}_image.png"), replace = true).toString
        }.fold {
          println("is")
          BadRequest(Messages("image.upload.failed"))
        } { imagePath =>
          println("succss")
          imageService.create(imagePath, data)
          Ok(Messages("image.upload.success"))
        }
      }
    )

  }

  def get(filename: String) = authAction { implicit request =>
    implicit val ec: ExecutionContext = cc.executionContext
    Ok.sendFile(
      content = new java.io.File(filename),
      inline = true
    )
  }
}
