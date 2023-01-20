package co.earthme.fqbot

import co.earthme.fqbot.command.CommandList
import co.earthme.fqbot.command.CommandParser
import co.earthme.fqbot.command.commandentry.RPIC3Command
import co.earthme.fqbot.command.commandentry.RPICCommand
import co.earthme.fqbot.manager.BotManager
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.manager.DataManager

suspend fun main() {
    ConfigManager.initConfig()
    DataManager.initOrRead()
    BotManager.readConfigArray()
    BotManager.initAllBot()
    //Add shutdown hook
    Runtime.getRuntime().addShutdownHook(Thread {
        CommandParser.getProcessWorker().shutdownNow()
    })
    //Register commands
    CommandList.regCommand(RPIC3Command())
    CommandList.regCommand(RPICCommand())
}