package co.earthme.fqbot.command.commandentry

import co.earthme.fqbot.command.PackagedCommandInfo
import co.earthme.fqbot.manager.ConfigManager
import co.earthme.fqbot.utils.PixivRandomPictureResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.ByteArrayInputStream
import java.util.stream.Stream
import kotlin.streams.toList

class RPICCommand : CommandEntry {
    override fun getName(): String {
        return "superWoc"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        val target = if (firedEvent is GroupMessageEvent){
            firedEvent.group
        }else{
            firedEvent.sender
        }

        val args = commandArg.getArgs()
        var gotLinks : Stream<String>? = null

        if (args.isNotEmpty()){
            if (args.size == 1){
                try {
                    val r18 = Integer.parseInt(args[0])
                    val got = PixivRandomPictureResponse.getNewLink(r18,1)
                    gotLinks = got?.let {
                        PixivRandomPictureResponse.getAllLinks(it)
                    }
                }catch (e : NumberFormatException){
                    target.sendMessage("Please support a int in the first arg!")
                }
            }
        }else{
            val got = PixivRandomPictureResponse.getNewLink(0,1)
            gotLinks = got?.let {
                PixivRandomPictureResponse.getAllLinks(it)
            }
        }

        val proxy = ConfigManager.getReadConfig().getReadProxy()
        for (link in gotLinks!!.toList()){
            var downloaded: ByteArray? = null
            proxy?.let {
                downloaded = PixivRandomPictureResponse.downloadFromLink(link, it)
            }
            downloaded?.let {
                val picStream = ByteArrayInputStream(downloaded)
                try {
                    val image = picStream.uploadAsImage(target,null)
                    target.sendMessage(image)
                }finally {
                    withContext(Dispatchers.IO) {
                        picStream.close()
                    }
                }
            }
        }
    }
}