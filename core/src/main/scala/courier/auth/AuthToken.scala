package courier.auth

/** Token de autenticación para un elemento que tiene un identificador único */
final case class AuthToken(id: String) extends AnyVal
