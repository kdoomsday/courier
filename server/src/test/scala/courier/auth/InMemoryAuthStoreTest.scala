package courier.auth

import utest._

object InMemoryAuthStoreTest extends TestSuite {
  val tests = Tests {
    'put - {
      'validatesAfterPut - {
        val store = InMemoryAuthStore()
        val id = ClientId("foo")
        val token = AuthToken("bar")
        assert(store.store(id, token).validate(id, token))
      }

      'notValidateWrongToken - {
        val store = InMemoryAuthStore()
        val id = ClientId("foo")
        val tokenA = AuthToken("bar")
        val tokenB = AuthToken("other")
        assert(!store.store(id, tokenA).validate(id, tokenB))
      }
    }
  }
}
