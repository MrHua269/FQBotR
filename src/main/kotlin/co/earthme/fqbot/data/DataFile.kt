package co.earthme.fqbot.data

import com.google.gson.Gson

class DataFile(
    var wocPicIndex: Int = 0
) {
    override fun toString(): String {
        return gson.toJson(this)
    }

    companion object {
        private val gson: Gson = Gson()
    }
}