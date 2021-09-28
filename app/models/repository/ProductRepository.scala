package models.repository

import models.{Category, Product}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val categoryRepository: CategoryRepository)
                                  (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import categoryRepository.CategoryTable
  import dbConfig._
  import profile.api._

  private val productTable = TableQuery[ProductTable]
  private val categoryTable = TableQuery[CategoryTable]

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def description: Rep[String] = column[String]("description")

    def category: Rep[Long] = column[Long]("category")

    def category_fk = foreignKey("cat_fk",category, categoryTable)(_.id)

    def * = (id, name, description, category) <> ((Product.apply _).tupled, Product.unapply)

  }

  def create(name: String, description: String, category: Long): Future[Product] = db.run {
    (productTable.map(p => (p.name, p.description,p.category))
      returning productTable.map(_.id)
      into {case ((name,description,category),id) => Product(id,name, description,category)}
      ) += (name, description, category)
  }

  def getAll(): Future[Seq[ProductDto]] = db.run {
    val joinQuery = for {
      (product, category) <- productTable join categoryTable on (_.category === _.id)
    } yield (product, category)
    joinQuery.result
      .map(_.toStream
        .map(product_category => ProductDto(product_category._1, product_category._2))
        .toList)
  }

  def getById(id: Long): Future[ProductDto] = db.run {
    val joinQuery = for {
      (product, category) <- productTable join categoryTable on (_.category === _.id)
    } yield (product, category)
    joinQuery.filter(_._1.id === id).result.head
      .map(product_category => ProductDto(product_category._1, product_category._2))
  }

  def getByIdOption(id: Long): Future[Option[ProductDto]] = db.run {
    val joinQuery = for {
      (product, category) <- productTable join categoryTable on (_.category === _.id)
    } yield (product, category)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(product_category) => Some(ProductDto(product_category._1, product_category._2))
        case None => None
      }
  }

  def update(id: Long, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(productTable.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(productTable.filter(_.id === id).delete).map(_ => ())
}
