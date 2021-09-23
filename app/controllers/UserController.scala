package controllers

import javax.inject._
import models.User
import models.repository.{UserRepository, UserDto}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserController @Inject()(usersRepo: UserRepository, cc: MessagesControllerComponents)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "email" -> email,
      "providerId" -> nonEmptyText,
      "providerKey" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "email" -> email,
      "providerId" -> nonEmptyText,
      "providerKey" -> nonEmptyText
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def getUsers: Action[AnyContent] = Action.async { implicit request =>
    val usersList = usersRepo.getAll
    usersList.map( users => Ok(views.html.user.users(users)))
  }

  def getUser(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val user = usersRepo.getByIdOption(id)
    user.map {
      case Some(u) => Ok(views.html.user.user(u))
      case None => Redirect(controllers.routes.UserController.getUsers)
    }
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val user = usersRepo.getById(id)
    user.map(u => {
      val userForm = updateUserForm.fill(UpdateUserForm(u.id, u.email, u.providerId, u.providerKey))
      Ok(views.html.user.userupdate(userForm))
    })
  }

  def updateUserHandle: Action[AnyContent] = Action.async { implicit request =>
    updateUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user.userupdate(errorForm))
        )
      },
      user => {
        usersRepo.update(user.id, UserDto(user.id, user.email, user.providerId, user.providerKey)).map { _ =>
          Redirect(controllers.routes.UserController.updateUser(user.id)).flashing("success" -> "user updated")
        }
      }
    )

  }

  def addUser: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.useradd(userForm))
  }

  def addUserHandle: Action[AnyContent] = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user.useradd(errorForm))
        )
      },
      user => {
        usersRepo.create(user.providerKey, user.providerId, user.email).map { _ =>
          Redirect(controllers.routes.UserController.addUser).flashing("success" -> "product.created")
        }
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    usersRepo.delete(id)
      .map(_ => Redirect(controllers.routes.UserController.getUsers))
  }
}

case class CreateUserForm(email: String, providerId: String, providerKey : String)
case class UpdateUserForm(id: Long, email: String, providerId: String, providerKey : String)
