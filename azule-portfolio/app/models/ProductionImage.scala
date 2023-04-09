package models

import skinny.orm.SkinnyJoinTable

case class ProductionImage(productionId: Long, imageId: Long)
object ProductionImage extends SkinnyJoinTable[ProductionImage] {
  override lazy val tableName = "production_images"
  override def defaultAlias = createAlias("pi")
}