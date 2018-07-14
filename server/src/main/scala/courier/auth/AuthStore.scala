package courier.auth

/** Almacén de data de autenticación.
  * Permite almacenar y obtener los tokens generados.
  */
trait AuthStore[A <: AuthStore[A]] {

  /** Almacenar el token de autenticación generado para unas credenciales
    * @param creds Las credenciales
    * @param token El token
    * @return Un AuthStore que contiene los datos almacenados
    */
  def store(creds: Credenciales, token: AuthToken): A

  /** Validar un par de credenciales con token
    * @param creds Las credenciales
    * @param token El token
    * @return Si el par Credenciales + Token existe en este store
    */
  def validate(creds: Credenciales, token: AuthToken): Boolean
}
