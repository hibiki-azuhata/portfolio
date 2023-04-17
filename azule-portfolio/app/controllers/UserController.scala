package controllers

import controllers.AuthAction.USER_NAME
import play.api.i18n.I18nSupport
import play.api.mvc.{BaseController, ControllerComponents}
import service.UserService

import javax.inject.Inject

class UserController @Inject()(
  userService: UserService,
  implicit val cc: ControllerComponents
) extends Controller with LoginSupport {

  def loginPage() = Action { implicit request =>
    Ok(views.html.auth.login(loginForm))
  }
  def login() = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      _ => BadRequest(views.html.auth.login(loginForm)),
      data => {
        userService.authenticate(data).fold {
          Unauthorized(views.html.auth.login(loginForm))
        } { name =>
          Redirect(routes.Productions.newProduction()).withSession(USER_NAME -> name)
        }
      }
    )
  }

}
