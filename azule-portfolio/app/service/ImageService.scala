package service

import models.Image

trait ImageService {

  def create(path: String, alt: String): Image

  def load(id: Int): Option[Image]

  def update(id: Int, alt: String): Option[Image]

  def delete(id: Int): Boolean

}
