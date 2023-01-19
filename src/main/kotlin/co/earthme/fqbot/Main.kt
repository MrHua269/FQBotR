package co.earthme.fqbot

import co.earthme.fqbot.command.CommandParser
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
}