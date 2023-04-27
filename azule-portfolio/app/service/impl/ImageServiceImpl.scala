package service.impl

import models.Image
import service.ImageService

class ImageServiceImpl extends ImageService {
  def create(path: String, alt: String): Image =
    Image.create(path, alt)

  def load(id: Int): Option[Image] =
    Image.findById(id)

  def update(id: Int, alt: String): Option[Image] = {
    Image.updateById(id).withNamedValues(Image.column.alt -> alt)
    Image.findById(id)
  }

  def delete(id: Int): Boolean =
    Image.deleteById(id) > 0

}
