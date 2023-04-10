package models

import scalikejdbc._
import skinny.orm.SkinnyJoinTable

case class ProductionImage(productionId: Long, imageId: Long)
object ProductionImage extends SkinnyJoinTable[ProductionImage] {
  override lazy val tableName = "production_images"
  override def defaultAlias = createAlias("pi")

  def create(productionId: Long, imageIds: Seq[Long]): Unit = {
    imageIds.foreach { id =>
      ProductionImage.createWithAttributes(
        Symbol("productionId") -> productionId,
        Symbol("imageId") -> id
      )
    } // TODO: sqlはまとめる batchなど
  }
}