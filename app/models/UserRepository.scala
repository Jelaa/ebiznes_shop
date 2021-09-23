package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val userTable = TableQuery[UserTable]

  def create(email: String): Future[User] = db.run {
    (userTable.map(u => (u.email))
      returning userTable.map(_.id)
      into ((name, id) => User(id, email))
      ) += (email)
  }

  def list(): Future[Seq[User]] = db.run {
    userTable.result
  }
  def getById(id: Long): Future[User] = db.run {
    userTable.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[User]] = db.run {
    userTable.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(userTable.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_user: User): Future[Unit] = {
    val userToUpdate: User = new_user.copy(id)
    db.run(userTable.filter(_.id === id).update(userToUpdate)).map(_ => ())
  }

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email")

    def * = (id, email) <> ((User.apply _).tupled, User.unapply)
  }


}
