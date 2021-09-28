package models.repository

import models.User
import play.api.libs.json.{Json, OFormat}

case class UserDto(id: Long, email: String, providerId : String, providerKey : String)

object UserDto {
  implicit val userDtoFormat: OFormat[UserDto] = Json.format[UserDto]

  def apply(user : User): UserDto =
    UserDto(user.id, user.email, user.loginInfo.providerID, user.loginInfo.providerKey)
}