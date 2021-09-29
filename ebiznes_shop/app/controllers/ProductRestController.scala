package controllers

import models.Product
import models.repository.ProductRepository
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, MessagesRequest}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ProductRestController @Inject()(productsRepo: ProductRepository, cc: ControllerComponents)
                                     (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val createFormatter: OFormat[CreateProduct] = Json.format[CreateProduct]
  implicit val updateFormatter: OFormat[UpdateProduct] = Json.format[UpdateProduct]

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val productsList = productsRepo.getAll
    productsList.map( products => Ok(Json.toJson(products)))
  }

  def getProduct(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val product = productsRepo.getByIdOption(id)
    product.map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound
    }
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val requestJson = request.body.asJson
    val requestBody = requestJson.flatMap(Json.fromJson[UpdateProduct](_).asOpt)
    requestBody match {
      case Some(productToUpdate) =>
        productsRepo.update(id, Product(id, productToUpdate.name, productToUpdate.description, productToUpdate.category))
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  def addProduct: Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateProduct](_).asOpt)
    requestBody match {
      case Some(newProduct) =>
        productsRepo.create(newProduct.name, newProduct.description, newProduct.category)
          .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    productsRepo.delete(id)
      .map(_ => Ok)
  }
}

case class CreateProduct(name: String, description: String, category: Long)
case class UpdateProduct(id: Long, name: String, description: String, category: Long)