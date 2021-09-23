package controllers

import models.repository.{UserDto, UserRepository}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, MessagesRequest}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserRestController @Inject()(userRepo: UserRepository, cc: ControllerComponents)
                                  (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val createFormatter: OFormat[CreateUser] = Json.format[CreateUser]
  implicit val updateFormatter: OFormat[UpdateUser] = Json.format[UpdateUser]

  def getUsers: Action[AnyContent] = Action.async { implicit request =>
    val usersList = userRepo.getAll
    usersList.map( u => Ok(Json.toJson(u)))
  }

  def getUser(id: Long) = Action.async { implicit request =>
    val user = userRepo.getByIdOption(id)
    user.map {
      case Some(u) => Ok(Json.toJson(u))
      case None => NotFound
    }
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[UpdateUser](_).asOpt)
    requestBody match {
      case Some(userToUpdate) =>
        userRepo.update(id, UserDto(id, userToUpdate.email, userToUpdate.providerId, userToUpdate.providerKey))
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  def addUser: Action[AnyContent] = Action.async { implicit request =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[CreateUser](_).asOpt)
    requestBody match {
      case Some(newUser) =>
        userRepo.create(newUser.email, newUser.providerId, newUser.providerKey)
          .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  def delete(id: Long) = Action.async {
    userRepo.delete(id)
      .map(_ => Ok)
  }

}

case class CreateUser(email: String, providerId : String, providerKey : String)

case class UpdateUser(email: String, providerId : String, providerKey : String)