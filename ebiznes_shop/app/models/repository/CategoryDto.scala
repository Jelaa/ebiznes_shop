package models.repository

import models.Category
import play.api.libs.json.{Json, OFormat}

case class CategoryDto(id: Long, name: String)

object CategoryDto {
  implicit val categoryDtoFormat: OFormat[CategoryDto] = Json.format[CategoryDto]

  def apply(category : Category): CategoryDto =
    CategoryDto(category.id, category.name)
}