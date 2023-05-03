package controllers

import controllers.AuthAction.LOGIN_SESSION
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.ControllerComponents
import service.UserService
import service.UserService.UserData

import javax.inject.Inject

class UserController @Inject()(
  userService: UserService,
  authAction: AuthAction,
  implicit val cc: ControllerComponents
) extends Controller with LoginSupport {

  private def userForm(isNew: Boolean)(implicit messages: Messages) = Form(
    mapping(
      "id" -> optional(longNumber).verifying(
        Messages("verifyId"),
        id => isNew || id.nonEmpty
      ),
      "username" -> nonEmptyText,
      "" -> tuple(
        "password" -> nonEmptyText,
        "verifyPassword" -> nonEmptyText
      ).verifying(Messages("verifyPass"), pass => pass._1 == pass._2)
        .transform[String](
          p => p._1,
          _ => ("", "")
        )
    )(UserData.apply)(UserData.unapply)
  )
  def login() = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      _ => BadRequest(views.html.start.login(loginForm)),
      data => {
        userService.createUUID(data).fold {
          Unauthorized(views.html.start.login(loginForm))
        } { uuid =>
          Ok(views.html.page.manual()).withSession(LOGIN_SESSION -> uuid)
        }
      }
    )
  }

  def logout() = Action { implicit request =>
    request.session.get(LOGIN_SESSION).fold {
      BadRequest(Messages("logout.error"))
    } { username =>
      Ok(Messages("logout.success", username)).removingFromSession(LOGIN_SESSION)
    }
  }

  def index() = authAction { implicit request =>
    Ok(views.html.page.manageUser(userService.list))
  }
  def createPage() = authAction { implicit request =>
    Ok(
      views.html.page.user.userForm(
        routes.UserController.create(),
        "window-edit-user-new",
        userForm(true)
      )
    )
  }

  def updatePage(id: Long) = authAction { implicit request =>
    Ok(
      views.html.page.user.userForm(
        routes.UserController.create(),
        s"window-edit-user-$id",
        userForm(false)
      )
    )
  }

  def create() = authAction { implicit request =>
    userForm(isNew = true).bindFromRequest().fold(
      formWithError =>
        BadRequest(
          views.html.page.user.userForm(
            routes.UserController.create(),
            "window-edit-user-new",
            formWithError
          )
        ),
      data => {
        userService.create(data)
        Ok(Messages("success"))
      }
    )
  }

  def update() = authAction { implicit request =>
    userForm(isNew = false).bindFromRequest().fold(
      formWithError =>
        BadRequest(
          views.html.page.user.userForm(
            routes.UserController.update(),
            s"window-edit-user-${formWithError("id").value.getOrElse("new")}",
            formWithError
          )
        ),
      data => {
        userService.update(data)
        Ok(Messages("success"))
      }
    )
  }

  def control() = authAction { implicit request =>
    Ok(views.html.start.control())
  }

}
