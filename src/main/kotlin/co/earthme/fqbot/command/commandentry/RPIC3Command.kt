package co.earthme.fqbot.command.commandentry

import co.earthme.fqbot.command.CommandParser
import co.earthme.fqbot.command.PackagedCommandInfo
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.manager.DataManager
import co.earthme.fqbot.utils.Utils
import co.earthme.fqbot.utils.Woc2UrlUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.ByteArrayInputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.LockSupport

class RPIC3Command : CommandEntry {
    private val lastWocUrls: Queue<String> = ConcurrentLinkedQueue()

    override fun getName(): String {
        return "woc"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        //自动装弹(bushi)
        if (lastWocUrls.isEmpty()) {
            //Add lock around.Because the wocPixIndex is not thread safe
            synchronized(this) {
                Woc2UrlUtil.getNewWocPicList(DataManager.getReadData().wocPicIndex)?.let { this.lastWocUrls.addAll(it) }
                DataManager.getReadData().wocPicIndex++
                //Save it because we already edit them
                DataManager.save()
            }
        }

        //Get the message nodes
        val nodes: List<ForwardMessage.Node> = getNewNodes(firedEvent.sender, Int.MAX_VALUE, firedEvent.sender)
        val preview: MutableList<String> = ArrayList()

        for (i in nodes.indices) {
            preview.add(firedEvent.sender.nameCardOrNick + ":" + "[图片]")
        }

        val message = ForwardMessage(preview, "Yee", "Yee", "Yee", "Yee", nodes)

        //Send it
        if (firedEvent is GroupMessageEvent) {
            firedEvent.group.sendMessage(message)
        } else if (firedEvent is FriendMessageEvent || firedEvent is GroupTempMessageEvent) {
            firedEvent.sender.sendMessage(message)
        }
    }


    private suspend fun getNewNodes(sender: User, time: Int, target: Contact): List<ForwardMessage.Node> {
        val nodes = CopyOnWriteArrayList<ForwardMessage.Node>()
        val taskCounter = AtomicInteger()
        val downloaded: MutableList<ByteArray> = Collections.synchronizedList(ArrayList())


        //Ensure thread safe because the steps are not atomic
        synchronized(this) {
            for (i in 1..6) {
                if (this.lastWocUrls.isEmpty()) {
                    break
                }
                taskCounter.getAndIncrement()
                CommandParser.getProcessWorker().execute {
                    try {
                        val bytes: ByteArray? = if (ConfigManager.getReadConfig().enableProxy()) {
                            val proxy = ConfigManager.getReadConfig().getReadProxy()
                            proxy?.let {
                                val linkHasBeenPolled = this.lastWocUrls.poll()
                                linkHasBeenPolled?.let {
                                    Utils.getBytes(linkHasBeenPolled,proxy)
                                }
                            }
                        } else {
                            Utils.getBytes(this.lastWocUrls.poll())
                        }

                        bytes?.let {
                            downloaded.add(it)
                        }
                    } finally {
                        taskCounter.getAndDecrement()
                    }
                }
            }
        }

        withContext(Dispatchers.IO) {
            while (taskCounter.get() > 0) {
                LockSupport.parkNanos(1)
            }
        }

        for (bytes in downloaded) {
            val picInputStream  = ByteArrayInputStream(bytes)
            try {
                val message: Image =  picInputStream.uploadAsImage(target, null)
                nodes.add(ForwardMessage.Node(sender.id, time, sender.nameCardOrNick, message))
            }finally {
                withContext(Dispatchers.IO) {
                    picInputStream.close()
                }
            }
        }
        return nodes
    }
}