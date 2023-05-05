package controllers

import controllers.AuthAction._
import controllers.AuthWithDemo.DUMMY_SESSION
import play.api.cache.SyncCacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.{BadRequest, Unauthorized}
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, ControllerComponents, PlayBodyParsers, Request, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthAction @Inject()(
  cache: SyncCacheApi,
  val cc: ControllerComponents,
  parsers: PlayBodyParsers
)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with LoginSupport with I18nSupport {
  override def parser: BodyParser[AnyContent] = parsers.anyContent
  override def invokeBlock[T](request: Request[T], block: Request[T] => Future[Result]): Future[Result] = {
    implicit val req: Request[T] = request
    req.session.get(LOGIN_SESSION) match {
      case Some(uuid) if cache.get[Long](uuid).nonEmpty =>
        block(request)
      case _ if req.session.get(DUMMY_SESSION).nonEmpty => Future(Unauthorized(views.html.page.demo()))
      case _ => Future(Unauthorized(views.html.start.login(loginForm)))
    }
  }

  override def messagesApi: MessagesApi = cc.messagesApi
}

object AuthAction {
  val LOGIN_SESSION = "login-uuid"
}
