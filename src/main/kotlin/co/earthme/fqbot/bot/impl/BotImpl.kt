package co.earthme.fqbot.bot.impl

import co.earthme.fqbot.bot.BotEntry
import co.earthme.fqbot.command.CommandParser
import co.earthme.fqbot.eventsystem.EventHub
import co.earthme.fqbot.eventsystem.EventListener
import net.mamoe.mirai.event.Event
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BotImpl : BotEntry() {
    override suspend fun processEvent(event: Event?) {
        event?.let {
            systemEventHub.dispatchEvent(it,this.bot!!)
            miscEventHub.dispatchEvent(it,this.bot!!)
        }
    }

    companion object {
        private val miscEventDispatchWorker: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        private val asyncSystemDispatchWorker: ExecutorService = Executors.newCachedThreadPool()
        private val systemEventHub : EventHub = EventHub(asyncSystemDispatchWorker)
        private val miscEventHub : EventHub = EventHub(miscEventDispatchWorker)

        init {
            systemEventHub.registerListener(CommandParser())
        }

        fun getSystemWorker(): ExecutorService{
            return asyncSystemDispatchWorker
        }

        fun registerEventListener(listener: EventListener){
            this.miscEventHub.registerListener(listener)
        }

        fun registerSystemEventListener(listener: EventListener){
            this.systemEventHub.registerListener(listener);
        }

        fun shutdownEventHubs(){
            systemEventHub.removeAll()
            miscEventHub.removeAll()

            miscEventDispatchWorker.shutdownNow()
            asyncSystemDispatchWorker.shutdownNow()
        }
    }
}