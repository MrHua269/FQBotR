package co.earthme.fqbot

import co.earthme.fqbot.command.CommandList
import co.earthme.fqbot.command.CommandParser
import co.earthme.fqbot.command.commandentry.RPIC3Command
import co.earthme.fqbot.manager.BotManager
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.manager.DataManager

suspend fun main() {
    ConfigManager.initConfig()
    DataManager.initOrRead()
    BotManager.readConfigArray()
    BotManager.initAllBot()
    Runtime.getRuntime().addShutdownHook(Thread {
        CommandParser.getProcessWorker().shutdownNow()
    })
    CommandList.regCommand(RPIC3Command())
}