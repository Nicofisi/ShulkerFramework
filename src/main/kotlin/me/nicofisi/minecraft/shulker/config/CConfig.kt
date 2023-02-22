package me.nicofisi.minecraft.shulker.config

import me.nicofisi.minecraft.shulker.PluginInfo
import me.nicofisi.minecraft.shulker.utils.logError
import me.nicofisi.minecraft.shulker.utils.logInfo
import me.nicofisi.minecraft.shulker.utils.logWarning
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.format.DateTimeFormatter

abstract class CConfig<T>(
    val configFile: Path,
    val wrapper: YamlConfigurationWrapper<T>,
    val latestConfigVersion: Int
) {
    var yaml: T = wrapper.newConfiguration()
    val entries = mutableListOf<COption<*>>()

    open fun afterReload() {}

    protected open fun handleVersionIncrease(config: T, fromVersion: Int) {}

    private fun parseConfig(map: Map<String, Any?>, ignoreProblems: Boolean = false) = map.forEach { (path, value) ->
        if (path != "config-version") {
            try {
                val option = entries.find { it.configPath == path }
                if (option != null) {
                    if (value != null)
                        option.deserialize(value)
                    else
                        option.resetDefaultValue()
                } else if (!ignoreProblems)
                    logWarning(
                        "There is an unknown path '$path' in the config file $configFile - it should be removed"
                    )
            } catch (ex: ClassCastException) {
                if (!ignoreProblems) {
                    logError("The value at path '$path' in $configFile is of non-matching type.")
                    logError("It will be replaced with a default value.")
                    logError("Please, use the default value to conclude what the correct format is,")
                    logError("and edit the configuration appropriately.")
                }
            }
        }
    }

    fun setMissingDefaultsAndVersion() {
        setMissingDefaultsInConfig(yaml)
        wrapper.set(yaml, "config-version", latestConfigVersion)
    }

    fun reloadConfig() {
        if (!Files.exists(configFile)) {
            setMissingDefaultsAndVersion()
            saveConfig()
        } else {
            require(Files.isRegularFile(configFile)) { "The config $configFile must be a regular file" }
            val yaml = wrapper.loadFromFile(configFile.toFile())
            this.yaml = yaml

            val savedConfigVersion = wrapper.get<Int>(yaml, "config-version")
            if (savedConfigVersion == null) {
                logWarning("The config file $configFile is missing the 'config-version' entry. Did you remove it?")
                logWarning("The plugin cannot function without this config entry.")
                logWarning("You need to delete this config file and restart the server for the config to be recreated.")
                throw IllegalStateException()
            }
            if (savedConfigVersion != latestConfigVersion) {
                val oldConfigsDir = configFile.parent.resolve("old-configs")
                if (!Files.exists(oldConfigsDir)) {
                    Files.createDirectories(oldConfigsDir)
                }
                if (savedConfigVersion > latestConfigVersion) { // TODO update message
                    val pluginName = PluginInfo.Spigot.plugin.name
                    logWarning("It appears that the highest config file version understandable by this version of $pluginName")
                    logWarning("is lower than the one in your current config file. Did you perhaps downgrade the plugin")
                    logWarning("or manually change the config version in the config file? Please never do that again.")
                    logWarning("To fix the issue you need to delete/move the config.yml, restart the server for the config")
                    logWarning("to be recreated, and manually fill it in again. Sorry, but that's not a fault of $pluginName!")
                    throw IllegalStateException()
                } else {
                    logInfo("The config $configFile is going to be updated from v$savedConfigVersion to v$latestConfigVersion now")
                    parseConfig(wrapper.getValues(yaml), ignoreProblems = true)
                    (savedConfigVersion until latestConfigVersion).forEach { fromVersion ->
                        logInfo("Updating the config from v$fromVersion to v$${fromVersion + 1}...")
                        handleVersionIncrease(yaml, fromVersion)
                    }
                    setMissingDefaultsAndVersion()
                    Files.move(
                        configFile, oldConfigsDir.resolve(
                            "${configFile.fileName}-v${savedConfigVersion}.yml"
                        )
                    )
                    saveConfig()
                    logInfo("The config has been successfully updated")
                }
            }
            setMissingDefaultsInConfig(yaml)

            parseConfig(wrapper.getValues(yaml))
        }

        afterReload()
    }

    fun saveConfig() {
        getConfigHeader()?.let {
            wrapper.setHeader(yaml, it)
        }
        entries.forEach {
            wrapper.set(yaml, it.configPath, it.serialize())
        }
        if (wrapper.get<Int>(yaml, "config-version") == null) {
            wrapper.set(yaml, "config-version", latestConfigVersion)
        }
        wrapper.saveToFile(yaml, configFile.toFile())
    }

    private fun setMissingDefaultsInConfig(yaml: T) {
        entries.filterNot { wrapper.contains(yaml, it.configPath) }
            .forEach {
                wrapper.set(yaml, it.configPath, it.value)
            }
    }

    fun getConfigHeader(): String? =
        if (configFile.fileName.toString() == "config.yml") {
            CConfig::class.java.classLoader?.getResource("config-header.md")?.readText()?.trim()
        } else null
}
