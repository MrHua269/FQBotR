package co.earthme.fqbot

import co.earthme.fqbot.bot.impl.BotImpl
import co.earthme.fqbot.command.CommandList
import co.earthme.fqbot.command.commandentry.RPIC3Command
import co.earthme.fqbot.command.commandentry.RPICCommand
import co.earthme.fqbot.command.commandentry.ReloadCommand
import co.earthme.fqbot.command.commandentry.StatsCommand
import co.earthme.fqbot.manager.BotManager
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.manager.DataManager
import co.earthme.fqbot.scripting.JSCommandLoader
import java.io.File
import java.util.Scanner

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


    })

    //Register commands
    CommandList.regCommand(RPIC3Command())
    CommandList.regCommand(RPICCommand())
    CommandList.regCommand(ReloadCommand())
    CommandList.regCommand(StatsCommand())

    val scanner = Scanner(System.`in`)

    while (scanner.hasNext()){
        BotManager.getMultiSender().send(scanner.nextLine(),ConfigManager.getReadConfig().getListeningGroup())
    }

    BotImpl.shutdownEventHubs()
    JSCommandLoader.clearAll()
    BotManager.shutdown()
}