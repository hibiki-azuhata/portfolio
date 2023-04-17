package controllers

import play.api.data.Form
import play.api.data.Forms._
import service.UserService.LoginData

trait LoginSupport {

  def loginForm = Form(mapping(
    "name" -> nonEmptyText,
    "password" -> nonEmptyText
  )(LoginData.apply)(LoginData.unapply))

}
