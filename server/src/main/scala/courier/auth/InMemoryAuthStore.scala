package courier.auth

import scala.collection.mutable.{ Map => MMap }

/** Implementación en memoria de un AuthStore */
class InMemoryAuthStore private (store: MMap[ClientId, AuthToken])
  extends AuthStore[InMemoryAuthStore] {

  override def store(id: ClientId, token: AuthToken): InMemoryAuthStore = {
    store.update(id, token)
    this
  }

  override def validate(id: ClientId, token: AuthToken): Boolean =
    store.contains(id) && store(id) == token
}

object InMemoryAuthStore {
  /** Crear un nuevo store vacío */
  def apply(): InMemoryAuthStore = new InMemoryAuthStore(MMap())
}
