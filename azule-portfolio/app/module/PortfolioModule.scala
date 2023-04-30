package module

import models.User
import module.PortfolioModule._
import play.api.inject.Module
import play.api.{Configuration, Environment}
import scalikejdbc.config.DBs
import service._
import service.impl._

import java.nio.file.{Files, Paths}

class PortfolioModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration) = {
    val dir = Paths.get(DATA_PATH)
    if(Files.notExists(dir)) Files.createDirectory(dir)
    val dirImages = Paths.get(IMAGE_PATH)
    if(Files.notExists(dirImages)) Files.createDirectory(dirImages)

    DBs.setupAll()
    if(User.findAll().isEmpty) {
      User.create("root", "root")
    }


    Seq(
      bind[ProductionService].to[ProductionServiceImpl],
      bind[ImageService].to[ImageServiceImpl],
      bind[UserService].to[UserServiceImpl],
      bind[PageService].to[PageServiceImpl]
    )
  }
}

object PortfolioModule {
  val DATA_PATH = "data"
  val IMAGE_PATH = s"${DATA_PATH}/images"
}
