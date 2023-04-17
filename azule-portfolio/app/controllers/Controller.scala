package controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{BaseController, ControllerComponents}

abstract class Controller(implicit override val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
}
