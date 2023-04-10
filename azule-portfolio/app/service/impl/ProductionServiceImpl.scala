package service.impl

import models.{Image, Production, ProductionImage, ProductionTag}
import scalikejdbc.sqls
import service.ProductionService
import service.ProductionService.ProductionData

class ProductionServiceImpl extends ProductionService {

  override def create(data: ProductionData): Production =
    Production.create(data.title, data.content, data.thumbnail, data.imageIds, data.tagIds)

  override def update(data: ProductionData): Unit = {
    val column = Production.column
    data.id.foreach { id =>
      Production.updateById(id).withNamedValues(
        column.title -> data.title,
        column.content -> data.content,
        column.thumbnail -> data.thumbnail
      )
      Production.findById(id).foreach { base =>
        ProductionTag.deleteBy(sqls.in(ProductionTag.column.tagId, base.tags.map(_.id)))
        ProductionImage.deleteBy(sqls.in(ProductionImage.column.imageId, base.images.map(_.id)))

        val oldImageIds = base.images.map(_.id).diff(data.imageIds)
        Image.deleteBy(sqls.in(Image.column.id, oldImageIds))
      }
      ProductionTag.create(id, data.tagIds)
      ProductionImage.create(id, data.imageIds)
    }
  }

  override def load: Seq[Production] =
    Production.findAll()

  override def remove(id: Long): Unit = {
    Production.deleteById(id)
    Image.deleteBy(sqls.in(
      Image.column.id,
      ProductionImage.findAllBy(sqls.eq(ProductionImage.column.productionId, id)).map(_.imageId)
    ))
    ProductionImage.deleteBy(sqls.eq(ProductionImage.column.productionId, id))
    ProductionTag.deleteBy(sqls.eq(ProductionTag.column.productionId, id))
  }


}
