package service

import models.Production
import service.ProductionService.ProductionData

trait ProductionService {

  def create(data: ProductionData): Production

  def update(data: ProductionData): Unit

  def load: Seq[Production]

  def remove(id: Long): Unit

}

object ProductionService {
  case class ProductionData(
    id: Option[Long],
    title: String,
    content: String,
    thumbnail: String,
    imageIds: Seq[Long],
    tagIds: Seq[Long]
  )

  case class ImageData(
    url: String,
    alt: String
  )
}
