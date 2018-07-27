package courier.auth

import java.util.UUID

/** Autenticador que siempre devuelve éxito, con un UUID random como token */
object DummyAutenticador extends Autenticador {

  def autenticar(id: ClientId): Either[AuthError, AuthInfo] =
    Right(AuthInfo(Node("unico"), AuthToken(UUID.randomUUID().toString)))
}
