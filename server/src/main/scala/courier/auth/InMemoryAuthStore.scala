package courier.auth

import scala.collection.mutable.{ Map => MMap }

/** Implementación en memoria de un AuthStore */
class InMemoryAuthStore private (store: MMap[Credenciales, AuthToken])
  extends AuthStore[InMemoryAuthStore] {

  override def store(creds: Credenciales, token: AuthToken): InMemoryAuthStore = {
    store.update(creds, token)
    this
  }

  override def validate(creds: Credenciales, token: AuthToken): Boolean =
    store.contains(creds) && store(creds) == token
}

object InMemoryAuthStore {
  /** Crear un nuevo store vacío */
  def apply(): InMemoryAuthStore = new InMemoryAuthStore(MMap())
}
