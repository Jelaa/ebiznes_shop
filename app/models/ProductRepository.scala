package models

import models.CategoryRepository
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
      ) += (name, description,category)
  }

  def list(): Future[Seq[Product]] = db.run {
    productTable.result
  }

  def getByCategory(category_id: Long): Future[Seq[Product]] = db.run {
    productTable.filter(_.category === category_id).result
  }

  def getById(id: Long): Future[Product] = db.run {
    productTable.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Product]] = db.run {
    productTable.filter(_.id === id).result.headOption
  }

  def getByCategories(category_ids: List[Long]): Future[Seq[Product]] = db.run {
    productTable.filter(_.category inSet category_ids).result
  }

  def delete(id: Long): Future[Unit] = db.run(productTable.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(productTable.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}
