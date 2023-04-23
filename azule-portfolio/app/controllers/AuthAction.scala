package controllers

import controllers.AuthAction._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, ControllerComponents, PlayBodyParsers, Request, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthAction @Inject()(val cc: ControllerComponents, parsers: PlayBodyParsers)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with LoginSupport with I18nSupport {
  override def parser: BodyParser[AnyContent] = parsers.anyContent
  override def invokeBlock[T](request: Request[T], block: Request[T] => Future[Result]): Future[Result] = {
    implicit val req: Request[T] = request
    req.session.get(USER_NAME) match {
      case Some(_) => block(request)
      case None => Future(Unauthorized(views.html.start.login(loginForm)))
    }
  }

  override def messagesApi: MessagesApi = cc.messagesApi
}

object AuthAction {
  val USER_NAME = "name"
}
