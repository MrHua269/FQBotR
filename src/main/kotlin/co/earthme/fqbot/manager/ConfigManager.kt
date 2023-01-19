package co.earthme.fqbot.manager

import co.earthme.fqbot.data.ConfigFile
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Files

class ConfigManager {
    companion object{
        private var configFile : ConfigFile = ConfigFile(114514,114514)
        private val configFileEntry : File = File("config.json")
        private val logger : Logger = LogManager.getLogger()
        private val gson : Gson = Gson()

        fun initConfig(){
            if (configFileEntry.exists()){
                val readBytes : ByteArray = Files.readAllBytes(configFileEntry.toPath())
                configFile = gson.fromJson(String(readBytes),ConfigFile::class.java)
                logger.info("Read config file!")
                return
            }
            logger.info("Creating config file")
            val bytes : ByteArray = configFile.toString().toByteArray()
            Files.write(configFileEntry.toPath(),bytes)
        }

        fun getReadConfig() : ConfigFile{
            return configFile
        }
    }
}