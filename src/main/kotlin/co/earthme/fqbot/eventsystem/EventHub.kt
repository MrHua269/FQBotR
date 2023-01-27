package co.earthme.fqbot.eventsystem

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event
import java.util.*
import java.util.concurrent.Executor

class EventHub (
    private val dispatchExecutor: Executor
){
    private val listeners: MutableList<EventListener> = Collections.synchronizedList(ArrayList())

    @Synchronized
    fun dispatchEvent(event: Event,dispatcherBot: Bot){
        for (listener in this.listeners){
            this.dispatchExecutor.execute {
                listener.onEvent(event,dispatcherBot)
            }
        }
    }

    @Synchronized
    fun registerListener(listener: EventListener){
        if (!this.listeners.contains(listener)){
            this.listeners.add(listener)
        }
    }

    @Synchronized
    fun removeListener(listener: EventListener){
        this.listeners.remove(listener)
    }

    @Synchronized
    fun removeListener(listenerName: String){
        for (listener in this.listeners){
            if(listener.getListenerName() == listenerName){
                this.listeners.remove(listener)
                break
            }
        }
    }

    @Synchronized
    fun removeAll(){
        for (listener in this.listeners){
            listener.onRemoved(this)
        }
        this.listeners.clear()
    }
}