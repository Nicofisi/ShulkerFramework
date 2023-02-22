package me.nicofisi.minecraft.shulker.config

import me.nicofisi.minecraft.shulker.PluginInfo
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Path

abstract class CBukkitConfig(configFile: Path, latestConfigVersion: Int) :
    CConfig<YamlConfiguration>(configFile, BukkitYamlConfigurationWrapper(), latestConfigVersion) {

    constructor(relativeFilePath: String, latestConfigVersion: Int) : this(
        PluginInfo.Spigot.plugin.dataFolder.toPath().resolve(relativeFilePath),
        latestConfigVersion
    )
}

class BukkitYamlConfigurationWrapper : YamlConfigurationWrapper<YamlConfiguration>() {
    override fun loadFromFile(file: File): YamlConfiguration = YamlConfiguration.loadConfiguration(file)

    override fun saveToFile(configuration: YamlConfiguration, file: File) {
        configuration.save(file)
    }

    override fun newConfiguration(): YamlConfiguration = YamlConfiguration()

    override fun contains(configuration: YamlConfiguration, path: String) = configuration.contains(path)

    override fun set(configuration: YamlConfiguration, path: String, value: Any?) {
        configuration.set(path, value)
    }

    override fun setHeader(configuration: YamlConfiguration, header: String) {
        configuration.options().header(header)
    }

    override fun getValues(configuration: YamlConfiguration): Map<String, Any?> = configuration.getValues(true)

    @Throws(ClassCastException::class)
    override fun <A> get(configuration: YamlConfiguration, path: String): A? {
        @Suppress("UNCHECKED_CAST")
        return getAny(configuration, path) as A?
    }

    override fun getAny(configuration: YamlConfiguration, path: String): Any? = configuration.get(path)
}
