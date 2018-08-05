package courier.client.auth

import cats.effect.IO
import courier.auth.{ AuthToken, ClientId }

/** Dao de autenticación para clientes */
trait AuthDao {
  /** Obtener el token de autenticación a partir de las credenciales */
  def getToken(creds: ClientId): IO[AuthToken]
}
