package co.earthme.fqbot.callbacks

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class NullContinuation : Continuation<Unit> {
    override val context: CoroutineContext
        get() = kotlin.coroutines.EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
    }
}