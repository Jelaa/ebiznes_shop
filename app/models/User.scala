package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.repository.UserDto
import play.api.libs.json.{Json, OFormat}

case class User(id: Long, loginInfo: LoginInfo, email: String) extends Identity

object User {
  implicit val loginInfoFormat: OFormat[LoginInfo] = Json.format[LoginInfo]
  implicit val userFormat: OFormat[User] = Json.format[User]

  def toModel(userDto : UserDto) : User = User(userDto.id, LoginInfo(userDto.providerId, userDto.providerKey), userDto.email)
}
