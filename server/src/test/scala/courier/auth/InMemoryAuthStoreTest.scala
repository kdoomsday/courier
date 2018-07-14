package courier.auth

import utest._

object InMemoryAuthStoreTest extends TestSuite {
  val tests = Tests {
    'put - {
      'validatesAfterPut - {
        val store = InMemoryAuthStore()
        val creds = Credenciales("foo")
        val token = AuthToken("bar")
        assert(store.store(creds, token).validate(creds, token))
      }

      'notValidateWrongToken - {
        val store = InMemoryAuthStore()
        val creds = Credenciales("foo")
        val tokenA = AuthToken("bar")
        val tokenB = AuthToken("other")
        assert(!store.store(creds, tokenA).validate(creds, tokenB))
      }
    }
  }
}
