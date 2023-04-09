package models

import skinny.orm.SkinnyJoinTable

case class ProductionTag(productionId: Long, tagId: Long)

object ProductionTag extends SkinnyJoinTable[ProductionTag] {
  override lazy val tableName = "production_tags"
  override def defaultAlias = createAlias("pt")
}
