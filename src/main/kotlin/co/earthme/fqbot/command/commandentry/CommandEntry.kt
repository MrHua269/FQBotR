package co.earthme.fqbot.command.commandentry

import co.earthme.fqbot.command.PackagedCommandInfo
import net.mamoe.mirai.event.events.MessageEvent

interface CommandEntry {
    fun getName() : String
    fun process(commandArg:PackagedCommandInfo,firedEvent : MessageEvent)
}