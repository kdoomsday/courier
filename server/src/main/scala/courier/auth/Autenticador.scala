package courier.auth

/** Maneja la autenticación de nodos */
trait Autenticador {

  /** Autenticar un nodo
    * @param id El identificador con el que se intenta autenticar
    * @return El error de autenticación si falló. Si fue exitosa el token de
    * autenticación correspondiente.
    */
  def autenticar(id: ClientId): Either[AuthError, AuthInfo]
}
