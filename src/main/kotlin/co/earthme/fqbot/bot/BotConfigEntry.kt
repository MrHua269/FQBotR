package co.earthme.fqbot.bot


import com.google.gson.Gson
import net.mamoe.mirai.utils.BotConfiguration


class BotConfigEntry(
    private val qid: Long,
    private val password: String,
    private val protocol: BotConfiguration.MiraiProtocol
) {
    fun getProtocol(): BotConfiguration.MiraiProtocol {
        return this.protocol
    }

    fun getQid(): Long {
        return this.qid
    }

    fun getPassword(): String {
        return this.password
    }

    override fun toString(): String {
        return GSON.toJson(this)
    }

    companion object {
        private val GSON: Gson = Gson()
    }
}