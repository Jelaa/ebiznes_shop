package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import models.Category
import models.repository.CategoryRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRestController @Inject()(categoryRepo: CategoryRepository, cc: ControllerComponents)
                                      (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val createFormatter: OFormat[CreateCategory] = Json.format[CreateCategory]
  implicit val updateFormatter: OFormat[UpdateCategory] = Json.format[UpdateCategory]

  def getCategories = Action.async { implicit request =>
    val categories = categoryRepo.getAll
    categories.map(c => Ok(Json.toJson(c)))
  }

  def getCategory(id: Long) = Action.async { implicit request =>
    categoryRepo.getByIdOption(id)
      .map {
        case Some(c) => Ok(Json.toJson(c))
        case None => NotFound
      }
  }

  def updateCategory(id: Long) = Action.async { implicit request =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[UpdateCategory](_).asOpt)
    requestBody match {
      case Some(categoryToUpdate) =>
        categoryRepo.update(id, Category(id, categoryToUpdate.name)).map(_ => Ok)
      case None => Future(BadRequest)}
  }

  def addCategory = Action.async { implicit request =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[CreateCategory](_).asOpt)
    requestBody match {
      case Some(newCategory) =>
        categoryRepo.create(newCategory.name)
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