package courier.auth

/** Maneja la autenticación de nodos */
trait Autenticador {

  /** Autenticar un nodo
    * @param creds Las credenciales con las que se intenta autenticar
    * @return El error de autenticación si falló. Si fue exitosa el token de
    * autenticación correspondiente.
    */
  def autenticar(creds: Credenciales): Either[AuthError, AuthToken]
}
