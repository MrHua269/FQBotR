package co.earthme.fqbot.eventsystem

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event

interface EventListener {
    fun onEvent(event: Event,dispatcher: Bot)

    fun getListenerName(): String

    fun onRemoved(eventHub: EventHub)
}