package co.earthme.fqbot.command

import co.earthme.fqbot.command.commandentry.CommandEntry
import java.util.concurrent.ConcurrentHashMap

class CommandList {
    companion object{
        private val registedCommands : MutableSet<CommandEntry> = ConcurrentHashMap.newKeySet()

        fun getAllCommands() : Set<CommandEntry>{
            return registedCommands
        }

        fun search(name : String) : CommandEntry? {
            for (command in registedCommands){
                if (command.getName() == name){
                    return command
                }
            }
            return null
        }

        fun removeCommand(commandEntry : CommandEntry){
            registedCommands.remove(commandEntry)
        }

        fun regCommand(commandEntry : CommandEntry){
            registedCommands.add(commandEntry)
        }
    }
}