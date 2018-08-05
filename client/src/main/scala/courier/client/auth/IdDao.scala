package courier.client.auth

import cats.effect.IO
import courier.auth.ClientId

trait IdDao {
  /** @return El Id de cliente a utilizar */
  def getId(): IO[ClientId]
}

/** Implementación de IdDao que genera un id aleatorio para utilizar */
object GeneratedIdDao extends IdDao {
  private[this] val id = ClientId(java.util.UUID.randomUUID().toString())

  def getId(): IO[ClientId] = IO(id)
}
