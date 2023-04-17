package service.impl

import models.User
import service.UserService
import service.UserService.{LoginData, UserData}

class UserServiceImpl extends UserService {
  def create(data: UserData): User =
    User.create(data.name, data.password)

  def update(data: UserData): Unit = {
    val column = User.column
    data.id.map { idValue =>
      User.updateById(idValue).withNamedValues(
        column.name -> data.name,
        column.password -> data.password
      )
    }
  }

  def remove(id: Long): Unit =
    User.deleteById(id)

  def load(id: Long): Option[User] =
    User.findById(id)

  def authenticate(data: LoginData): Option[String] =
    User.getUser(data.name, data.password).map(_.name)

}
