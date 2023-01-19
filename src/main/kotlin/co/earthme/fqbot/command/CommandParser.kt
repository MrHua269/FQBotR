package co.earthme.fqbot.command

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.MessageEvent
import java.lang.NullPointerException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.startCoroutine

class CommandParser {
    companion object{
        private val currListener : AtomicReference<Bot> = AtomicReference()
        private val processWorker = Executors.newCachedThreadPool()

        fun getProcessWorker() : ExecutorService{
            return processWorker
        }

        fun processEvent(event:Event){
            if (event is MessageEvent){
                if (currListener.get() == null || !currListener.get().isOnline){
                    currListener.set(event.bot)
                }
                if (currListener.get().equals(event.bot)){
                    processWorker.execute {
                        val processTask : suspend () -> Unit = {
                            fireProcess(event)
                        }
                        processTask.startCoroutine(CommandProcessCallBack())
                    }
                }
            }
        }

        private fun fireProcess(event:MessageEvent){
            val commandInfo = PackagedCommandInfo(event.message)
            try {
                CommandList.search(commandInfo.getHead())?.process(commandInfo, event)
            } catch (_: NullPointerException) {
                //Do not process npe because when command parse failed.It will throw a NPE
            }
        }
    }
}