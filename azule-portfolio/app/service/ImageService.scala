package service

import models.Image

trait ImageService {

  def create(path: String, alt: String): Image

  def load(id: Long): Option[Image]

  def list: Seq[Image]

  def update(id: Long, alt: String): Option[Image]

  def delete(id: Long): Boolean

}
