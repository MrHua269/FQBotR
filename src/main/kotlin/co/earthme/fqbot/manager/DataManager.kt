package co.earthme.fqbot.manager

import co.earthme.fqbot.data.DataFile
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Files

class DataManager {
    companion object {
        private val gson: Gson = Gson()
        private val logger: Logger = LogManager.getLogger()
        private val dataFile: File = File("botdata.json")
        private var currentDataFile: DataFile = DataFile(0)


        fun initOrRead() {
            logger.info("Reading data file")
            if (dataFile.exists()) {
                val readBytes: ByteArray = Files.readAllBytes(dataFile.toPath())
                currentDataFile = gson.fromJson(String(readBytes), DataFile::class.java)
                logger.info("Data file read!")
                return
            }
            val bytes: ByteArray = currentDataFile.toString().toByteArray()
            Files.write(dataFile.toPath(), bytes)
            logger.info("Data file created!")
        }

        fun save() {
            val bytes: ByteArray = currentDataFile.toString().toByteArray()
            Files.write(dataFile.toPath(), bytes)
        }

        fun getReadData(): DataFile {
            return currentDataFile
        }
    }
}