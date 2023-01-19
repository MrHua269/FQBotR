package co.earthme.fqbot.data

import com.google.gson.Gson

class ConfigFile(
    private val listenGroup : Long,
    private val masterQid : Long,
    private val paraLoadBots : Boolean
){
    fun getListeningGroup() : Long{
        return this.listenGroup
    }

    fun paraLoad() : Boolean{
        return this.paraLoadBots
    }

    fun getMasterQid() : Long{
        return this.masterQid
    }

    override fun toString(): String {
        return GSON.toJson(this)
    }

    companion object{
        private val GSON : Gson = Gson()
    }
}