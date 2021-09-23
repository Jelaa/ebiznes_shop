package controllers

import models._
import models.repository._

import javax.inject._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ProductController @Inject()(productsRepo: ProductRepository, categoryRepo: CategoryRepository, cc: MessagesControllerComponents)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val productsList = productsRepo.getAll
    productsList.map( products => Ok(views.html.product.products(products)))
  }

  def getProduct(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val product = productsRepo.getByIdOption(id)
    product.map {
      case Some(p) => Ok(views.html.product.product(p))
      case None => Redirect(routes.ProductController.getProducts)
    }
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepo.getAll.flatMap(categories => {
      productsRepo.getById(id).map(product => {
        val prodForm = updateProductForm.fill(UpdateProductForm(id, product.name, product.description, product.category.id))
        Ok(views.html.product.productupdate(prodForm, categories))
      })
    })
  }

  def updateProductHandle: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.getAll.flatMap(categories => {
      updateProductForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.product.productupdate(errorForm, categories))
          )
        },
        product => {
          productsRepo.update(product.id, Product(product.id, product.name, product.description, product.category))
            .map {_ =>
            Redirect(routes.ProductController.updateProduct(product.id)).flashing("success" -> "product updated")
          }
        }
      )
    })
  }

  def addProduct: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.getAll
    categories.map(cat => Ok(views.html.product.productadd(productForm, cat)))
  }

  def addProductHandle: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.getAll.flatMap(categories => {
      productForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.product.productadd(errorForm, categories))
          )
        },
        product => {
          productsRepo.create(product.name, product.description, product.category).map { _ =>
            Redirect(routes.ProductController.addProduct).flashing("success" -> "product.created")
          }
        }
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    productsRepo.delete(id)
      .map(_ => Redirect(routes.ProductController.getProducts))
  }
}

case class CreateProductForm(name: String, description: String, category: Long)
case class UpdateProductForm(id: Long, name: String, description: String, category: Long)
