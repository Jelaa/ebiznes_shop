package models.product

import models.product.Product
import models.category.CategoryRepository
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, categoryRepository: CategoryRepository)
                                  (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import categoryRepository.CategoryTable
  import dbConfig._
  import profile.api._

  private val product = TableQuery[ProductTable]
  private val cat = TableQuery[CategoryTable]

  private class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def description: Rep[String] = column[String]("description")

    def category: Rep[Int] = column[Int]("category")

    def category_fk = foreignKey("cat_fk",category, cat)(_.id)

    def * = (id, name, description, category) <> ((Product.apply _).tupled, Product.unapply)

  }


  def create(name: String, description: String, category: Int): Future[Product] = db.run {
    (product.map(p => (p.name, p.description,p.category))
      returning product.map(_.id)
      into {case ((name,description,category),id) => Product(id,name, description,category)}
      ) += (name, description,category)
  }

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getByCategory(category_id: Int): Future[Seq[Product]] = db.run {
    product.filter(_.category === category_id).result
  }

  def getById(id: Long): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def getByCategories(category_ids: List[Int]): Future[Seq[Product]] = db.run {
    product.filter(_.category inSet category_ids).result
  }

  def delete(id: Long): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}
