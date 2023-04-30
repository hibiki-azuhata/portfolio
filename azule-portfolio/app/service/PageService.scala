package service

import models.Page
import models.PageSupport.ContentType

trait PageService {
  def update(contentType: ContentType, content: String): Page

  def load(contentType: ContentType): Page

}
