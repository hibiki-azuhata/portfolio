package controllers

import module.PortfolioModule.IMAGE_PATH
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{ControllerComponents, MultipartFormData, Request}
import service.ImageService

import java.nio.file.Paths
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class Images @Inject()(
  authAction: AuthAction,
  authDemoAction: AuthWithDemo,
  val imageService: ImageService
)(implicit val cc: ControllerComponents) extends Controller {

  private def imageForm = Form(
    "alt" -> nonEmptyText
  )

  def index() = authDemoAction { implicit request =>
    Ok(views.html.page.manageImage(imageService.list))
  }

  def remove(id: Long) = authAction { implicit request =>
    imageService.load(id).foreach { image =>
      new java.io.File(s"$IMAGE_PATH/${Images.getName(image.url)}").delete()
    }
    imageService.delete(id)
    Ok
  }

  def show(id: Long) = authDemoAction { implicit request =>
    imageService.load(id).fold {
      BadRequest("")
    } { image =>
      Ok(views.html.page.image.show(image))
    }
  }

  def upload() = authAction(parse.multipartFormData) { implicit request =>
    imageForm.bindFromRequest().fold(
      _ => {
        BadRequest(s"""{"success": false, "file":"", "message":"image.upload.noAlt"}""")
      },
      data => {
        Images.upload("image", s"${data}_").fold {
          BadRequest(s"""{"success": false, "file":"", "message":"image.upload.failed"}""")
        } { imagePath =>
          imageService.create(imagePath, data)
          Ok(s"""{"success": true, "file":"${routes.Images.get(Images.getName(imagePath)).url}", "message":"image.upload.success"}""")
        }
      }
    )
  }

  def get(filename: String) = Action { implicit request =>
    implicit val ec: ExecutionContext = cc.executionContext
    Ok.sendFile(
      content = new java.io.File(s"$IMAGE_PATH/$filename"),
      inline = true
    )
  }
}

object Images {

  def getName(filename: String): String =
    filename.replaceFirst(s"$IMAGE_PATH/", "")
  def upload(key: String, prefix: String = "", suffix: String = "")(implicit request: Request[MultipartFormData[TemporaryFile]]): Option[String] = {
    request.body.file(key).map { pic =>
      pic.ref.copyTo(Paths.get(s"$IMAGE_PATH/$prefix$key$suffix.png"), replace = true).toString
    }
  }
}
