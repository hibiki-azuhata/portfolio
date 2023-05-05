package service.impl

import models.{Image, Production, ProductionImage}
import scalikejdbc.sqls
import service.ProductionService
import service.ProductionService.ProductionData

class ProductionServiceImpl extends ProductionService {

  override def create(data: ProductionData): Production = {
    val imageIds = data.images.map { i =>
      Image.create(i.url, i.alt).id
    }
    Production.create(data.title, data.content, data.thumbnail, imageIds)
  }

  override def update(data: ProductionData): Option[Production] = {
    val column = Production.column
    data.id.flatMap { id =>
      Production.updateById(id).withNamedValues(
        column.title -> data.title,
        column.content -> data.content,
        column.thumbnail -> data.thumbnail
      )
      Production.findById(id).foreach { base =>
        ProductionImage.deleteBy(sqls.in(ProductionImage.column.imageId, base.images.map(_.id)))

        //val oldImageIds = base.images.map(_.id).diff(data.imageIds)
        //Image.deleteBy(sqls.in(Image.column.id, oldImageIds))
      }
      //ProductionImage.create(id, data.imageIds)

      Production.findById(id)
    }
  }

  def find(id: Long): Option[Production] =
    Production.findById(id)

  override def load: Seq[Production] =
    Production.findAll()

  override def remove(id: Long): Unit = {
    Production.deleteById(id)
    Image.deleteBy(sqls.in(
      Image.column.id,
      ProductionImage.findAllBy(sqls.eq(ProductionImage.column.productionId, id)).map(_.imageId)
    ))
    ProductionImage.deleteBy(sqls.eq(ProductionImage.column.productionId, id))
  }


}
