package service

import models.User
import service.UserService.{LoginData, UserData}

trait UserService {

  def create(data: UserData): User

  def update(data: UserData): Unit

  def remove(id: Long): Unit

  def load(id: Long): Option[User]

  def list: Seq[User]

  def createUUID(data: LoginData): Option[String]

}

object UserService {
  case class UserData(id: Option[Long], username: String, password: String)

  case class LoginData(name: String, password: String)
}
