package co.earthme.fqbot.command

import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PackagedCommandInfo(chain: MessageChain) {
    private var commandHead: String? = null
    private val args: MutableList<String> = ArrayList()
    private val otherArgs = HashMap<Class<out Message?>, MutableList<Message>>()

    init {
        var textCounter = 0
        for (singleMessage in chain) {
            if (singleMessage is PlainText && textCounter == 0) {
                textCounter++
                val deserialized = tryDeserializeHead(singleMessage.contentToString())
                if (deserialized != null) {
                    this.commandHead = deserialized[0][0]
                    for (singleArg : String? in deserialized[1]){
                        singleArg?.let { this.args.add(it) }
                    }
                } else {
                    this.args.addAll(singleMessage.contentToString().split(" "))
                }
                continue
            }
            val messageClass: Class<out Message?> = singleMessage.javaClass
            if (!this.otherArgs.containsKey(messageClass)) {
                this.otherArgs[messageClass] = ArrayList()
            }
            this.otherArgs[messageClass]!!.add(singleMessage)
        }
    }

    fun getArgs(): List<String> {
        return this.args
    }

    fun getOtherArgs(): Map<Class<out Message?>, MutableList<Message>> {
        return this.otherArgs
    }

    fun getHead() : String?{
        return this.commandHead
    }

    override fun toString(): String {
        return String.format(
            "@PackagedCommandInfo[head=%s,str_arg=%s,other_args=%s]",
            this.commandHead, this.args, this.otherArgs
        )
    }

    companion object {
        private fun tryDeserializeHead(input: String): Array<Array<String?>>? {
            val fixed = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (fixed.isNotEmpty()) {
                val commandHead = fixed[0]
                val args = arrayOfNulls<String>(fixed.size - 1)
                System.arraycopy(fixed, 1, args, 0, fixed.size - 1)
                if (commandHead.startsWith("#") && commandHead.length > 1) {
                    val result = Array(2) {
                        arrayOfNulls<String>(
                            args.size.coerceAtLeast(1)
                        )
                    }
                    result[0][0] = commandHead.substring(1)
                    System.arraycopy(args, 0, result[1], 0, args.size)
                    return result
                }
            }
            return null
        }
    }
}