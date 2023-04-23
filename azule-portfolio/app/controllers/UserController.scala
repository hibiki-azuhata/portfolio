package controllers

import controllers.AuthAction.USER_NAME
import play.api.mvc.ControllerComponents
import service.UserService

import javax.inject.Inject

class UserController @Inject()(
  userService: UserService,
  authAction: AuthAction,
  implicit val cc: ControllerComponents
) extends Controller with LoginSupport {
  def login() = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      _ => BadRequest(views.html.start.login(loginForm)),
      data => {
        userService.authenticate(data).fold {
          Unauthorized(views.html.start.login(loginForm))
        } { name =>
          Ok(views.html.page.manual()).withSession(USER_NAME -> name)
        }
      }
    )
  }

  def control() = authAction { implicit request =>
    Ok(views.html.start.control())
  }

}
