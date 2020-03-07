package me.nicofisi.minecraft.shulker.config

import me.nicofisi.minecraft.shulker.PluginInfo
import me.nicofisi.minecraft.shulker.utils.dataFolder
import me.nicofisi.minecraft.shulker.utils.logError
import me.nicofisi.minecraft.shulker.utils.logInfo
import me.nicofisi.minecraft.shulker.utils.logWarning
import java.nio.file.Files

abstract class CConfig<T>(val wrapper: YamlConfigurationWrapper<T>) {
    val configFile = dataFolder.resolve("config.yml")
    var yaml: T? = null

    abstract fun loadDefaultValues()

    abstract fun getConfigDefaults(): Map<String, Any>

    @Throws(ClassCastException::class)
    abstract fun parseConfigEntry(key: String, value: Any)

    abstract fun afterReload()

    protected open fun handleVersionIncrease(config: T, fromVersion: Int) {}

    fun parseConfig(map: Map<String, Any?>) = map.forEach { (path, value) ->
        if (path != "config-version") {
            if (value == null) {
                logWarning("The config key '$path' has no assigned value, so the default value will be used")
            } else {
                try {
                    parseConfigEntry(path, value)
                } catch (ex: ClassCastException) {
                    logError("The value at path '$path' in $configFile is of non-matching type.")
                    logError("It will be replaced with a default value.")
                    logError("Please, use the default value to conclude what the correct format is,")
                    logError("and edit the configuration appropriately.")
                }
            }
        }
    }

    fun reloadConfig() {
        loadDefaultValues()

        val currentConfigVersion = javaClass.classLoader.getResource("current-config-version.txt")
            ?.readText()?.trim()?.toInt()
            ?: error("The file current-config-version.txt is missing from the file $dataFolder}")

        if (!Files.exists(configFile)) {
            val yaml = wrapper.newConfiguration()
            this.yaml = yaml
            setDefaultsInConfig(yaml)
            wrapper.set(yaml, "config-version", currentConfigVersion)
            getConfigHeader()?.let {
                wrapper.setHeader(yaml, it)
            }
            wrapper.saveToFile(yaml, configFile.toFile())
        } else {
            require(Files.isRegularFile(configFile)) { "config.yml must be a regular file" }
            val yaml = wrapper.loadFromFile(configFile.toFile())
            this.yaml = yaml

            val savedConfigVersion = wrapper.get<Int>(yaml, "config-version")
            if (savedConfigVersion == null) {
                logWarning("Your config file is missing the 'config-version' entry. Did you remove it?")
                logWarning("The plugin cannot function without this config entry.")
                logWarning("You need to delete the config.yml and restart the server for the config to be recreated.")
                throw IllegalStateException()
            }
            if (savedConfigVersion != currentConfigVersion) {
                val oldConfigsDir = dataFolder.resolve("old-configs")
                if (!Files.exists(oldConfigsDir)) {
                    Files.createDirectories(oldConfigsDir)
                }
                if (savedConfigVersion > currentConfigVersion) {
                    val pluginName = PluginInfo.Spigot.plugin.name
                    logWarning("It appears that the highest config file version understandable by this version of $pluginName")
                    logWarning("is lower than the one in your current config file. Did you perhaps downgrade the plugin")
                    logWarning("or manually change the config version in the config file? Please never do that again.")
                    logWarning("To fix the issue you need to delete/move the config.yml, restart the server for the config")
                    logWarning("to be recreated, and manually fill it in again. Sorry, but that's not a fault of $pluginName!")
                    throw IllegalStateException()
                } else {
                    logInfo("The config is going to be updated from v$savedConfigVersion to v$currentConfigVersion now")
                    (savedConfigVersion until currentConfigVersion).forEach { fromVersion ->
                        logInfo("Updating the config from v$fromVersion to v$currentConfigVersion...")
                        handleVersionIncrease(yaml, fromVersion)
                    }
                    setDefaultsInConfig(yaml)
                    getConfigHeader()?.let {
                        wrapper.setHeader(yaml, it)
                    }
                    wrapper.set(yaml, "config-version", currentConfigVersion)
                    // TODO ISO 8601 format
                    Files.move(configFile, oldConfigsDir.resolve("config-${System.currentTimeMillis()}.yml"))
                    wrapper.saveToFile(yaml, configFile.toFile())
                    logInfo("The config has been successfully updated")
                }
            }
            parseConfig(wrapper.getValues(yaml))
        }

        afterReload()
    }


    fun setDefaultsInConfig(yaml: T) {
        getConfigDefaults().filterNot { (key, _) -> wrapper.contains(yaml, key) }.forEach { (key, value) ->
            wrapper.set(yaml, key, value)
        }
    }

    fun getConfigHeader(): String? =
        CConfig::class.java.classLoader?.getResource("config-header.md")?.readText()?.trim()

    protected fun logParseElseWarning(key: String) {
        logWarning("Unknown config key: $key")
    }
}
