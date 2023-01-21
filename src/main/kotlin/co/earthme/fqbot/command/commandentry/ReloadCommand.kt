package co.earthme.fqbot.command.commandentry

import co.earthme.fqbot.command.PackagedCommandInfo
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.scripting.JSCommandLoader
import net.mamoe.mirai.event.events.MessageEvent

class ReloadCommand : CommandEntry {
    override fun getName(): String {
        return "reload"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        if (firedEvent.sender.id == ConfigManager.getReadConfig().getMasterQid()){
            JSCommandLoader.reload()
        }
    }
}