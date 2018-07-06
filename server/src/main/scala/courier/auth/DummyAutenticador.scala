package courier.auth

import java.util.UUID

/** Autenticador que siempre devuelve éxito, con un UUID random como token */
object DummyAutenticador extends Autenticador {

  def autenticar(creds: Credenciales): Either[AuthError, AuthToken] =
    Right(AuthToken(Node("unico"), UUID.randomUUID().toString))
}
