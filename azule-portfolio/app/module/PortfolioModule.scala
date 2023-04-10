package module

import play.api.inject.Module
import play.api.{Configuration, Environment}
import scalikejdbc.config.DBs
import service._
import service.impl._

class PortfolioModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration) = {
    DBs.setupAll()

    Seq(
      bind[ProductionService].to[ProductionServiceImpl]
    )
  }
}
