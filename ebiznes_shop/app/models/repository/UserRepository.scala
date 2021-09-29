package models.repository

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext, implicit val classTag: ClassTag[PasswordInfo])
                                extends IdentityService[User] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val userTable = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[UserDto](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email")

    def providerId = column[String]("providerId")

    def providerKey = column[String]("providerKey")

    def * = (id, email, providerId, providerKey) <> (data => UserDto.apply(data._1, data._2, data._3, data._4), UserDto.unapply)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    userTable.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
  }.map(x => {
  System.out.println(x)
  x.map(User.apply)
})

  def create(email: String, providerId: String, providerKey: String): Future[UserDto] = {
    db.run(
      userTable.filter(_.providerId === providerId)
        .filter(_.providerKey === providerKey)
        .filter(_.email === email)
        .result
        .headOption
    ).flatMap {
      case Some(usr) => Future.successful(usr)
      case None => db.run(
        (userTable.map(u => (u.email, u.providerId, u.providerKey))
          returning userTable.map(_.id)
          into { case ((email, providerId, providerKey), id) => UserDto(id, email, providerId, providerKey) }
          ) += (email, providerId, providerKey)
      )
    }
  }

  def getAll: Future[List[UserDto]] = db.run {
    userTable.result.map(_.toStream
      .toList)
  }

  def getByIdOption(id: Long): Future[Option[UserDto]] = db.run {
    userTable.filter(_.id === id).result.headOption
  }

  def getById(id: Long): Future[UserDto] = db.run {
    userTable.filter(_.id === id).result.head
  }

  def update(id: Long, newUser: UserDto): Future[Unit] = {
    val userToUpdate = newUser.copy(id)
    db.run {
      userTable.filter(_.id === id)
        .update(userToUpdate)
        .map(_ => userToUpdate).map(_ => ())
    }
  }

  def delete(id: Long): Future[Unit] =
    db.run {
      userTable.filter(_.id === id)
        .delete
        .map(_ => ())
    }
}
