package co.earthme.fqbot.command

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class CommandProcessCallBack : Continuation<Unit> {
    override val context: CoroutineContext
        get() = kotlin.coroutines.EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
    }
}