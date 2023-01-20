package co.earthme.fqbot.manager

import co.earthme.fqbot.bot.BotConfigEntry
import co.earthme.fqbot.bot.BotEntry
import co.earthme.fqbot.bot.impl.BotImpl
import co.earthme.fqbot.callbacks.PackagedContinuation
import co.earthme.fqbot.data.BotConfigEntryArray
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.utils.BotConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.startCoroutine
import kotlin.system.exitProcess

class BotManager {
    companion object {
        private val paraLoadExecutor: ExecutorService = Executors.newCachedThreadPool()
        private val configArrayFile: File = File("bots.json")
        private val loadedBots: MutableList<BotEntry> = Collections.synchronizedList(ArrayList())
        private var currentConfigArray: BotConfigEntryArray? = null
        private val gson: Gson = Gson()
        private val logger: Logger = LogManager.getLogger()

        fun readConfigArray() {
            if (configArrayFile.exists()) {
                val readBytes: ByteArray = Files.readAllBytes(configArrayFile.toPath())
                currentConfigArray = gson.fromJson(String(readBytes), BotConfigEntryArray::class.java)
                logger.info("Bot config loaded!")
            } else {
                currentConfigArray = BotConfigEntryArray(Array(2) {
                    BotConfigEntry(
                        114514,
                        "a",
                        BotConfiguration.MiraiProtocol.ANDROID_PAD
                    )
                    BotConfigEntry(
                        1145142,
                        "a",
                        BotConfiguration.MiraiProtocol.ANDROID_PAD
                    )
                })
                val bytes: ByteArray = currentConfigArray.toString().toByteArray()
                Files.write(configArrayFile.toPath(), bytes)
                logger.info(
                    "Please complete your config and start the bot again.Config file:{}",
                    configArrayFile.toPath()
                )
                exitProcess(0)
            }
        }

        suspend fun initAllBot() {
            if (currentConfigArray == null) {
                throw IllegalStateException("Config didn't init yet!")
            }
            logger.info("Init bots")
            val paraLoad = ConfigManager.getReadConfig().paraLoad()
            if (paraLoad) {
                val countDownLatch = CountDownLatch(currentConfigArray!!.getConfigEntries().size)
                for (botConfigEntry in currentConfigArray!!.getConfigEntries()) {
                    paraLoadExecutor.execute {
                        logger.info("Loading bot {}", botConfigEntry.getQid())
                        val botEntry: BotEntry = BotImpl()
                        val loginAction: suspend () -> Unit = {
                            try {
                                botEntry.runBot(botConfigEntry)
                            } catch (e: Exception) {
                                logger.error("Error in loading bot!")
                            }
                        }
                        loginAction.startCoroutine(PackagedContinuation {
                            try {
                                if (botEntry.bot!!.isOnline) {
                                    loadedBots.add(botEntry)
                                }
                            } finally {
                                countDownLatch.countDown()
                            }
                        })
                    }
                }
                withContext(Dispatchers.IO) {
                    countDownLatch.await()
                }
            } else {
                for (botConfigEntry in currentConfigArray!!.getConfigEntries()) {
                    logger.info("Loading bot {}", botConfigEntry.getQid())
                    try {
                        val botEntry: BotEntry = BotImpl()
                        botEntry.runBot(botConfigEntry)
                        loadedBots.add(botEntry)
                    } catch (e: Exception) {
                        logger.error("Error in loading bot!")
                        e.printStackTrace()
                    }
                }
            }
            logger.info("Inited {} bots", loadedBots.size)
            paraLoadExecutor.shutdownNow()
        }
    }
}