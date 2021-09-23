package models.repository

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
                                extends IdentityService[User] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val userTable = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[UserDto](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def providerId = column[String]("providerId")

    def providerKey = column[String]("providerKey")

    def email = column[String]("email")

    def * = (id, providerId, providerKey, email) <> ((UserDto.apply _).tupled, UserDto.unapply)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    userTable.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
  }.map(_.map(User.toModel))

  def create(providerId: String, providerKey: String, email: String): Future[UserDto] = db.run {
    (userTable.map(c => (c.providerId, c.providerKey, c.email))
      returning userTable.map(_.id)
      into { case ((providerId, providerKey, email), id) => UserDto(id, providerId, providerKey, email) }
      ) += (providerId, providerKey, email)
  }

  def getAll: Future[Seq[UserDto]] = db.run {
    userTable.result.map(_.toStream
      .toSeq)
  }

  def getByIdOption(id: Long): Future[Option[UserDto]] = db.run {
    userTable.filter(_.id === id).result.headOption
  }

  def getById(id: Long): Future[UserDto] = db.run {
    userTable.filter(_.id === id).result.head
  }

  def update(id: Long, newUser: UserDto): Future[UserDto] = {
    val userToUpdate = newUser.copy(id)
    db.run {
      userTable.filter(_.id === id)
        .update(userToUpdate)
        .map(_ => userToUpdate)
    }
  }

  def delete(id: Long): Future[Unit] =
    db.run {
      userTable.filter(_.id === id)
        .delete
        .map(_ => ())
    }
}
