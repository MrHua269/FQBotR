package co.earthme.fqbot.bot

import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.utils.BotConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.annotations.NotNull
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

abstract class BotEntry {
    @Volatile
    var bot: Bot? = null
        private set
    private var configEntry: BotConfigEntry? = null
    private val connected = AtomicBoolean(false)

    suspend fun runBot(@NotNull configEntry: BotConfigEntry) {
        this.configEntry = configEntry
        val configuration: BotConfiguration = object : BotConfiguration() {
            init {
                fileBasedDeviceInfo(File(DATA_FOLDER, "deviceInfo-" + configEntry.getQid() + ".json").path)
            }
        }
        configuration.protocol = configEntry.getProtocol()
        configuration.noBotLog()
        configuration.noNetworkLog()
        doInitCacheFolder(configuration, configEntry)
        this.bot = BotFactory.INSTANCE.newBot(configEntry.getQid(), configEntry.getPassword(), configuration)
        this.bot!!.login()
        this.bot!!.eventChannel.subscribeAlways<Event> { event ->
            processEvent(event)
        }
        this.connected.set(true)
    }

    fun getConfigEntry(): BotConfigEntry? {
        return this.configEntry
    }

    private fun doInitCacheFolder(configuration: BotConfiguration, entry: BotConfigEntry) {
        val folder = File(DATA_FOLDER, "caches-" + entry.getQid())
        if (!folder.exists()) {
            folder.mkdir()
        }
        configuration.cacheDir = folder
    }

    abstract fun processEvent(event: Event?)

    fun isConnected(): Boolean {
        return this.connected.get()
    }

    val currentQid: Long? get() = this.configEntry?.getQid()

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
        private val DATA_FOLDER = File("deviceInfoFolder")

        init {
            try {
                val infoFile = File("deviceInfoFolder")
                if (!infoFile.exists()) {
                    infoFile.mkdir()
                }
            } catch (e: Exception) {
                LOGGER.error(e)
            }
        }
    }
}