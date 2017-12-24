package courier.auth

/** Token de autenticación para un elemento
  * El token tiene un identificador (único) y un nodo para el cual es válido
  */
final case class AuthToken(nodo: Node, id: String)
