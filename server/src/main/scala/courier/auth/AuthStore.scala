package courier.auth

/** Almacén de data de autenticación.
  * Permite almacenar y obtener los tokens generados.
  */
trait AuthStore[A <: AuthStore[A]] {

  /** Almacenar el token de autenticación generado para unas credenciales
    * @param id    El id del cliente
    * @param token El token
    * @return Un AuthStore que contiene los datos almacenados
    */
  def store(id: ClientId, token: AuthToken): A

  /** Validar que un cliente tiene el token correcto
    * @param id    El identificador del cliente
    * @param token El token
    * @return Si el par ClientId + Token existe en este store
    */
  def validate(id: ClientId, token: AuthToken): Boolean

  /** Validar a partir de las credenciales */
  final def validate(c: Credenciales): Boolean = validate(c.id, c.token)
}
