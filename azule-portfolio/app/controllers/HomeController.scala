package controllers

import models.Production

import javax.inject._
import play.api._
import play.api.mvc._
import scalikejdbc.DB
import scalikejdbc.config.DBs

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    DBs.setupAll()
    DB localTx { implicit session =>
      Production.createWithAttributes(
        Symbol("value") -> "test",
        Symbol("count") -> 100
      )
      println("===================")
      println(Production.findAll())
    }
    Ok(views.html.index())
  }
}
