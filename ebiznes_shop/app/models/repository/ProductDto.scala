package models.repository

import models._
import play.api.libs.json.{Json, OFormat}

case class ProductDto(id: Long, name: String, description: String, category: CategoryDto)

object ProductDto {
  implicit val productDtoFormat: OFormat[ProductDto] = Json.format[ProductDto]

  def apply(product : Product, category : Category): ProductDto =
    ProductDto(product.id, product.name, product.description, CategoryDto(category))
}
