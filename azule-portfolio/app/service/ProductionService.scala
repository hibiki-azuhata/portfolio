package service

import models.Production
import service.ProductionService.ProductionData

trait ProductionService {

  def create(data: ProductionData): Production

  def update(data: ProductionData): Option[Production]

  def find(id: Long): Option[Production]

  def load: Seq[Production]

  def remove(id: Long): Unit

}

object ProductionService {
  case class ProductionData(
    id: Option[Long],
    title: String,
    content: String,
    thumbnail: String,
    images: Seq[ImageData],
    tagIds: Seq[Long]
  )

  case class ProductionInfoData(
    id: Option[Long],
    title: String,
    content: String,
    thumbnail: String
  ) {
    def toProductionData(thumbnail: String, images: Seq[ImageData], tagIds: Seq[Long]) = {
      ProductionData(
        this.id,
        this.title,
        this.content,
        thumbnail,
        images,
        tagIds
      )
    }
  }

  object ProductionInfoData {
    def toProductionData(p: Production) = {
      ProductionInfoData(
        Some(p.id),
        p.title,
        p.content,
        p.thumbnail
      )
    }
  }

  case class ImageData(
    url: String,
    alt: String
  )
}
