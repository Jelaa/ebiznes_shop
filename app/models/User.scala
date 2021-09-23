package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, email: String)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}
