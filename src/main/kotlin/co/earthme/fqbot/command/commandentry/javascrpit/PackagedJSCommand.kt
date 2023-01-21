package co.earthme.fqbot.command.commandentry.javascrpit

import co.earthme.fqbot.command.PackagedCommandInfo
import co.earthme.fqbot.command.commandentry.CommandEntry
import net.mamoe.mirai.event.events.MessageEvent
import javax.script.Invocable
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class PackagedJSCommand(
    private val script : Invocable
) : CommandEntry{
    fun getScript(): Invocable{
        return this.script
    }

    override fun getName(): String {
        return this.script.invokeFunction("getName", arrayOfNulls<JvmType.Object>(0)) as String
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        script.invokeFunction("process", arrayOf(commandArg,firedEvent))
    }
}