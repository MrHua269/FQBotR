package co.earthme.fqbot.command.commandentry

import co.earthme.fqbot.bot.impl.BotImpl
import co.earthme.fqbot.command.CommandParser
import co.earthme.fqbot.command.PackagedCommandInfo
import co.earthme.fqbot.manager.BotManager
import co.earthme.fqbot.manager.ConfigManager
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent

class StatsCommand : CommandEntry {
    override fun getName(): String {
        return "stats"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        if (firedEvent.sender.id == ConfigManager.getReadConfig().getMasterQid()){
            val statusMessage = StringBuilder()
            statusMessage.append("Current listener:").append(CommandParser.getCurrentListener().id).append("\n")
            statusMessage.append("Current system worker pool status:").append(BotImpl.getSystemWorker()).append("\n")
            statusMessage.append("Current master qid:").append(ConfigManager.getReadConfig().getMasterQid()).append("\n")
            statusMessage.append("Inited bot count:").append(BotManager.getInited().size)
            if (firedEvent is GroupMessageEvent){
                firedEvent.group.sendMessage(statusMessage.toString())
            }else{
                firedEvent.sender.sendMessage(statusMessage.toString())
            }
        }
    }
}