package service.impl

import models.User
import play.api.cache.SyncCacheApi
import service.UserService
import service.UserService.{LoginData, UserData}

import java.util.UUID
import javax.inject.Inject

class UserServiceImpl @Inject()(cache: SyncCacheApi) extends UserService {
  def create(data: UserData): User =
    User.create(data.username, data.password)

  def update(data: UserData): Unit = {
    val column = User.column
    data.id.map { idValue =>
      User.updateById(idValue).withNamedValues(
        column.name -> data.username,
        column.password -> data.password
      )
    }
  }

  def remove(id: Long): Unit =
    User.deleteById(id)

  def load(id: Long): Option[User] =
    User.findById(id)


  def list: Seq[User] = User.findAll()

  def createUUID(data: LoginData): Option[String] =
    User.getUser(data.name, data.password).map { user =>
      val uuid = UUID.randomUUID().toString
      cache.set(uuid, user.id)
      uuid
    }

}
