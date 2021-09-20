package controllers

import javax.inject._
import models.user._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserController @Inject()(usersRepo: UserRepository, cc: MessagesControllerComponents)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "email" -> email,
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "email" -> email,
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def getUsers: Action[AnyContent] = Action.async { implicit request =>
    val usersList = usersRepo.list()
    usersList.map( users => Ok(views.html.user.users(users)))
  }

  def getUser(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val user = usersRepo.getByIdOption(id)
    user.map {
      case Some(u) => Ok(views.html.user.user(u))
      case None => Redirect(routes.UserController.getUsers)
    }
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val user = usersRepo.getById(id)
    user.map(u => {
      val userForm = updateUserForm.fill(UpdateUserForm(u.id, u.email))
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
        usersRepo.update(user.id, User(user.id, user.email)).map { _ =>
          Redirect(routes.UserController.updateUser(user.id)).flashing("success" -> "user updated")
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
        usersRepo.create(user.email).map { _ =>
          Redirect(routes.UserController.addUser).flashing("success" -> "product.created")
        }
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    usersRepo.delete(id)
      .map(_ => Redirect(routes.UserController.getUsers))
  }
}

case class CreateUserForm(email: String)
case class UpdateUserForm(id: Long, email: String)
