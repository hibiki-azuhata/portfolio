package module

import play.api.inject.Module
import play.api.{Configuration, Environment}
import scalikejdbc.config.DBs
import service._
import service.impl._

import java.nio.file.{Files, Paths}

class PortfolioModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration) = {
    val dir = Paths.get("data")
    if(Files.notExists(dir)) Files.createDirectory(dir)

    DBs.setupAll()

    Seq(
      bind[ProductionService].to[ProductionServiceImpl]
    )
  }
}
