package minek.redux

interface State

interface Action

typealias Reducer <S> = (previousState: S, action: Action) -> S

typealias StoreSubscriber <S> = (S) -> Unit

interface Store<S : State> {
    fun dispatch(action: Action)
    fun subscribe(subscriber: StoreSubscriber<S>): Boolean
    fun unsubscribe(subscriber: StoreSubscriber<S>): Boolean
    fun current(): S
}

class DefaultStore<S : State>(
    initialState: S,
    private val reducer: Reducer<S>
) : Store<S> {
    private var state: S = initialState
        set(value) {
            field = value
            subscribers.forEach { it(value) }
        }

    private val subscribers = mutableSetOf<StoreSubscriber<S>>()

    override fun dispatch(action: Action) {
        state = reducer(state, action)
    }

    override fun subscribe(subscriber: StoreSubscriber<S>): Boolean = subscribers.add(subscriber)
    override fun unsubscribe(subscriber: StoreSubscriber<S>): Boolean = subscribers.remove(subscriber)
    override fun current(): S = state
}
