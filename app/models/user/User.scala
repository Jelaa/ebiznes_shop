package models.user

import play.api.libs.json.Json

case class User(id: Long, email: String)

object User {
  implicit val userFormat = Json.format[User]
}
