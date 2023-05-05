package service.impl

import models.Image
import service.ImageService

class ImageServiceImpl extends ImageService {
  def create(path: String, alt: String): Image =
    Image.create(path, alt)

  def load(id: Long): Option[Image] =
    Image.findById(id)

  def list: Seq[Image] =
    Image.findAll()

  def update(id: Long, alt: String): Option[Image] = {
    Image.updateById(id).withNamedValues(Image.column.alt -> alt)
    Image.findById(id)
  }

  def delete(id: Long): Boolean =
    Image.deleteById(id) > 0

}
