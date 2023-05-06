package controllers

import controllers.AuthAction.LOGIN_SESSION
import controllers.AuthWithDemo.DUMMY_SESSION
import models.User
import play.api.cache.SyncCacheApi
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.ControllerComponents
import service.UserService
import service.UserService.UserData

import javax.inject.Inject

class UserController @Inject()(
  cache: SyncCacheApi,
  userService: UserService,
  authAction: AuthAction,
  authDemoAction: AuthWithDemo,
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
      _ => BadRequest(views.html.start.login(loginForm, true)),
      data => {
        userService.createUUID(data).fold {
          BadRequest(views.html.start.login(loginForm, true))
        } { uuid =>
          Ok.withSession(LOGIN_SESSION -> uuid)
        }
      }
    )
  }

  def demoLogin() = Action { implicit request =>
    Ok.withSession(DUMMY_SESSION -> "dummy")
  }

  def loginInfo() = authDemoAction { implicit request =>
    val user = request.session.get(LOGIN_SESSION) match {
      case Some(uuid) if cache.get[Long](uuid).nonEmpty =>
        cache.get[Long](uuid).flatMap(userService.load)
      case _ if request.session.get(DUMMY_SESSION).nonEmpty =>
        Some(User(-1, Messages("user.demo.username"), ""))
      case _ => None
    }
    Ok(views.html.start.loginInfo(user))
  }

  def logout() = Action { implicit request =>
    request.session.get(LOGIN_SESSION).fold {
      request.session.get(DUMMY_SESSION).fold {
        BadRequest(Messages("logout.error"))
      } { _ =>
        Ok(Messages("logout.success")).removingFromSession(DUMMY_SESSION)
      }
    } { username =>
      Ok(Messages("logout.success", username)).removingFromSession(LOGIN_SESSION)
    }
  }

  def index() = authAction { implicit request =>
    Ok(views.html.page.manageUser(userService.list))
  }

  def edit(idOpt: Option[Long]) = authAction { implicit request =>
    idOpt.flatMap(userService.load).map { user =>
      (user.id, UserData(Some(user.id), user.name, user.password))
    }.fold {
      Ok(
        views.html.page.user.userForm(
          routes.UserController.create(),
          "window-edit-user-new",
          userForm(true)
        )
      )
    } { case (id, data) =>
      Ok(
        views.html.page.user.userForm(
          routes.UserController.update(),
          s"window-edit-user-$id",
          userForm(false).fill(data)
        )
      )
    }
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
        Ok
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
        Ok
      }
    )
  }

  def remove(id: Long) = authAction { implicit request =>
    userService.remove(id)
    Ok
  }

  def demoAlert() = Action { implicit request =>
    Ok(views.html.page.demo())
  }

  def control() = authDemoAction { implicit request =>
    Ok(views.html.start.control())
  }

}
