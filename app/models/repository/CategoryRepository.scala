package models.repository

import models.Category
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  val categoryTable = TableQuery[CategoryTable]

  def create(name: String): Future[Category] = db.run {
    (categoryTable.map(c => (c.name))
      returning categoryTable.map(_.id)
      into ((name, id) => Category(id, name))
      ) += (name)
  }


  def getAll: Future[Seq[CategoryDto]] = db.run {
    categoryTable.result.map(_.toStream
      .map(CategoryDto.apply)
      .toSeq)
  }


  def getById(id: Long): Future[CategoryDto] = db.run {
    categoryTable.filter(_.id === id).result.head
      .map(CategoryDto.apply)
  }


  def getByIdOption(id: Long): Future[Option[CategoryDto]] = db.run {
    categoryTable.filter(_.id === id).result.headOption
      .map {
        case Some(c) => Some(CategoryDto(c))
        case None => None
      }
  }


  def update(id: Long, new_category: Category): Future[Unit] = {
    val categoryToUpdate: Category = new_category.copy(id)
    db.run {
      categoryTable.filter(_.id === id)
        .update(categoryToUpdate)
        .map(_ => ())
    }
  }

  def delete(id: Long): Future[Unit] =
    db.run {
      categoryTable.filter(_.id === id)
        .delete
        .map(_ => ())
    }
}

