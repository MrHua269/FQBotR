package co.earthme.fqbot.callbacks

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class BotLoadCallback(
    private val callBack : Runnable
) : Continuation<Unit>{
    override val context: CoroutineContext
    get() = kotlin.coroutines.EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        try {
            this.callBack.run()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}