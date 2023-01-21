package co.earthme.fqbot

import co.earthme.fqbot.command.CommandList
import co.earthme.fqbot.command.CommandParser
import co.earthme.fqbot.command.commandentry.RPIC3Command
import co.earthme.fqbot.command.commandentry.RPICCommand
import co.earthme.fqbot.command.commandentry.ReloadCommand
import co.earthme.fqbot.manager.BotManager
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.manager.DataManager
import co.earthme.fqbot.scripting.JSCommandLoader
import java.io.File

suspend fun main() {
    val scriptFolder = File("jsscripts")

    if (!scriptFolder.exists()){
        scriptFolder.mkdir()
    }

    ConfigManager.initConfig()
    DataManager.initOrRead()
    JSCommandLoader.loadAll(scriptFolder)
    BotManager.readConfigArray()
    BotManager.initAllBot()
    //Add shutdown hook
    Runtime.getRuntime().addShutdownHook(Thread {
        JSCommandLoader.clearAll()
        CommandParser.getProcessWorker().shutdownNow()
    })
    //Register commands
    CommandList.regCommand(RPIC3Command())
    CommandList.regCommand(RPICCommand())
    CommandList.regCommand(ReloadCommand())
}