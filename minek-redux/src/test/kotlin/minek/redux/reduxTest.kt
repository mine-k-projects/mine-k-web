package minek.redux

import org.junit.jupiter.api.Test

class ReduxTest {

    val PojoStateReducer: Reducer<PojoState> = { previousState, action ->
        when (action) {
            is PojoActions.Init -> PojoState("", "")
            is PojoActions.SetFoo -> previousState.copy(foo = action.foo)
            is PojoActions.SetBar -> previousState.copy(bar = action.bar)
            else -> previousState
        }
    }

    data class PojoState(val foo: String, val bar: String) : State

    sealed class PojoActions : Action {
        object Init : PojoActions()
        class SetFoo(val foo: String) : PojoActions()
        class SetBar(val bar: String) : PojoActions()
    }

    @Test
    fun test() {
        val store = DefaultStore(initialState = PojoState("", ""), reducer = PojoStateReducer)
        store.subscribe {
            println(it)
        }
        store.dispatch(PojoActions.SetFoo(foo = "test"))
        store.dispatch(PojoActions.SetBar(bar = "test"))
    }
}