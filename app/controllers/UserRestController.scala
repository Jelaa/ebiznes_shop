package controllers

import models.User
import play.api.mvc.{Action, AnyContent, MessagesRequest}

import scala.concurrent.Future

class UserRestController {

  def getUsers: Action[AnyContent] = Action.async { implicit request =>
    val usersList = usersRepo.list()
    usersList.map( u => Ok(Json.toJson(u)))
  }

  def getUser(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val user = usersRepo.getByIdOption(id)
    user.map {
      case Some(u) => Ok(Json.toJson(u))
      case None => NotFound
    }
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[UpdateUser](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        userRepo.update(id, userToUpdate.email)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  def addUser: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.useradd(userForm))
  }


  def delete(id: Long): Action[AnyContent] = Action.async {
    usersRepo.delete(id)
      .map(_ => Redirect(controllers.routes.UserController.getUsers))
  }

}
