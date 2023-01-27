package co.earthme.fqbot.multibot

import co.earthme.fqbot.bot.BotEntry
import co.earthme.fqbot.bot.impl.BotImpl
import co.earthme.fqbot.callbacks.NullContinuation
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.LockSupport
import kotlin.coroutines.startCoroutine

class MultiBotSender {
    private val messageSendQueue: Deque<Map<Long, Message>> = ConcurrentLinkedDeque()
    private val pollPos = AtomicBoolean(true)

    private val isRunning = AtomicBoolean(false)
    private val activeCount = AtomicInteger()

    fun shutdown() {
        isRunning.set(false)
        while (activeCount.get() > 0) {
            LockSupport.parkNanos(this, 1)
        }
        messageSendQueue.clear()
        pollPos.set(true)
    }

    fun doInit(bots: Collection<BotEntry>) {
        isRunning.set(true)
        for (botEntry in bots) {
            BotImpl.getSystemWorker().execute {
                val pollPosCP = pollPos.get()
                while (isRunning.get()) {
                    try {
                        if (pollPosCP) {
                            this.messageSendQueue.pollFirst()?.let {
                                it.forEach { (gid: Long, outMessage: Message?) ->
                                    gid.let {
                                        val sendAction: suspend () -> Unit = {
                                            botEntry.bot?.getGroup(it)?.sendMessage(outMessage)
                                        }

                                        sendAction.startCoroutine(NullContinuation())
                                    }
                                }
                            }
                        } else {
                            this.messageSendQueue.pollLast()?.let {
                                it.forEach { (gid: Long, outMessage: Message?) ->
                                    gid.let {
                                        val sendAction: suspend () -> Unit = {
                                            botEntry.bot?.getGroup(it)?.sendMessage(outMessage)
                                        }

                                        sendAction.startCoroutine(NullContinuation())
                                    }
                                }
                            }
                        }
                        if (messageSendQueue.isEmpty()) {
                            LockSupport.parkNanos("Free Sleep", 50_000_000L)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                activeCount.getAndDecrement()
            }
            activeCount.getAndIncrement()
            pollPos.set(!pollPos.get())
        }
    }

    fun send(message: Message, gid: Long) {
        val task = HashMap<Long, Message>()
        task[gid] = message
        messageSendQueue.add(task)
    }

    fun send(message: String, gid: Long) {
        val messageContent = PlainText(message)
        this.send(messageContent, gid)
    }
}