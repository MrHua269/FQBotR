package co.earthme.fqbot.scripting

import co.earthme.fqbot.command.CommandList
import co.earthme.fqbot.command.commandentry.javascrpit.PackagedJSCommand
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import javax.script.Invocable
import javax.script.ScriptEngineManager


class JSCommandLoader {
    companion object{
        private val loadedCommands: MutableSet<PackagedJSCommand> = ConcurrentHashMap.newKeySet()
        private val logger = LogManager.getLogger()
        private var currentScriptDir: File? = null

        private fun registerToCommandList(){
            for (command in loadedCommands){
                CommandList.regCommand(command)
            }
        }

        private fun loadSingleJavaScript(dataArray: ByteArray) {
            try {
                val engine = ScriptEngineManager().getEngineByName("javascript")
                engine.eval(String(dataArray, StandardCharsets.UTF_8))
                val inv = engine as Invocable
                loadedCommands.add(PackagedJSCommand(inv))
            } catch (e: Exception) {
                logger.error("Error in loading script!", e)
                e.printStackTrace()
            }
        }

        @Synchronized
        fun loadAll(scriptsDir: File?) {
            if (scriptsDir!!.exists()) {
                CompletableFuture.allOf(
                    *Arrays.stream(scriptsDir.listFiles())
                    .map { singleFile ->
                        CompletableFuture.runAsync {
                            try {
                                if (singleFile.name.endsWith(".js")) {
                                    val read = Files.readAllBytes(singleFile.toPath())
                                    loadSingleJavaScript(read)
                                }
                            } catch (e: Exception) {
                                logger.error("Error in reading file!", e)
                                e.printStackTrace()
                            }
                        }
                    }.toArray {
                        length -> arrayOfNulls<CompletableFuture<Any>>(length)
                    }).join()
                registerToCommandList()
                logger.info("Load {} javascripts!" , loadedCommands.size)
                currentScriptDir = scriptsDir
            }
        }

        @Synchronized
        fun clearAll(){
            for (singleCommand in CommandList.getAllCommands()){
                if (singleCommand is PackagedJSCommand){
                    CommandList.removeCommand(singleCommand)
                }
            }
            loadedCommands.clear()
        }

        @Synchronized
        fun reload() {
            clearAll()
            loadAll(currentScriptDir)
        }
    }
}