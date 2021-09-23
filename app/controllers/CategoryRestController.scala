package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import models.{Category, CategoryRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRestController @Inject()(categoryRepo: CategoryRepository, cc: ControllerComponents)
                                      (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCategories: Action[AnyContent] = Action.async { implicit request =>
    val categories = categoryRepo.list()
    categories.map(c => Ok(Json.toJson(c)))
  }

  def getCategory(id: Long): Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.getByIdOption(id)
      .map {
        case Some(c) => Ok(Json.toJson(c))
        case None => NotFound
      }
  }

  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[UpdateCategory](_).asOpt)
    requestBody match {
      case Some(categoryToUpdate) =>
        categoryRepo.update(id, categoryToUpdate.name).map(_ => Ok)
      case None => Future(BadRequest)}
  }

  def addCategory: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[CreateCategory](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        categoryRepo.create(newItem.name)
          .map(c => Created(Json.toJson(c)))
      case None =>
        Future(BadRequest)
    }
  }


  def delete(id: Long): Action[AnyContent] = Action.async {
    categoryRepo.delete(id)
      .map(_ => Ok)
  }
}

case class CreateCategory(name: String)

case class UpdateCategory(name: String)