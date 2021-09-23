package controllers

import models._
import javax.inject._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)
                                  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
    "name" -> nonEmptyText
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

    val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
    "id" -> longNumber,
    "name" -> nonEmptyText
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def getCategories: Action[AnyContent] = Action.async { implicit request =>
    val categories = categoryRepo.list()
    categories.map(c => Ok(views.html.category.categories(c)))
  }

  def getCategory(id: Long): Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.category.category(value))
        case None => Redirect(routes.CategoryController.getCategories)
      }
  }

  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepo.getById(id).map(category => {
      val categoryForm = updateCategoryForm.fill(UpdateCategoryForm(id, category.name))
      Ok(views.html.category.categoryupdate(categoryForm))
    })
  }

  def updateCategoryHandle = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.category.categoryupdate(errorForm))),
      category => categoryRepo.update(category.id, Category(category.id, category.name))
        .map(_ => Redirect(routes.CategoryController.updateCategory(category.id)).flashing("success" -> "category updated"))
    )
  }

  def addCategory: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.category.categoryadd(categoryForm))
  }

  def addCategoryHandle = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.category.categoryadd(errorForm))),
      category => categoryRepo.create(category.name)
        .map(_ => Redirect(routes.CategoryController.addCategory).flashing("success" -> "category created"))
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    categoryRepo.delete(id)
      .map(_ => Redirect(controllers.routes.CategoryController.getCategories))
  }
}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(id: Long, name: String)
