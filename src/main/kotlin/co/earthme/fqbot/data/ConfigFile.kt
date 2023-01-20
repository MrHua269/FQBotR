package co.earthme.fqbot.data

import co.earthme.fqbot.manager.ConfigManager
import com.google.gson.Gson
import java.net.InetSocketAddress
import java.net.Proxy

class ConfigFile(
    private val listenGroup: Long,
    private val masterQid: Long,
    private val paraLoadBots: Boolean,
    private val enableProxy: Boolean,
    private val proxyIp: String,
    private val proxyPort: Int
) {
    fun enableProxy(): Boolean {
        return this.enableProxy
    }

    fun getReadProxy(): Proxy?{
        if (this.enableProxy){
            return Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress(
                    ConfigManager.getReadConfig().getProxyIp(),
                    ConfigManager.getReadConfig().getProxyPort()
                )
            )
        }
        return null
    }

    private fun getProxyIp(): String {
        return this.proxyIp
    }

    private fun getProxyPort(): Int {
        return this.proxyPort
    }

    fun getListeningGroup(): Long {
        return this.listenGroup
    }

    fun paraLoad(): Boolean {
        return this.paraLoadBots
    }

    fun getMasterQid(): Long {
        return this.masterQid
    }

    override fun toString(): String {
        return GSON.toJson(this)
    }

    companion object {
        private val GSON: Gson = Gson()
    }
}