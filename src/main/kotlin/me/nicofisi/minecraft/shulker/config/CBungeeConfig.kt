package me.nicofisi.minecraft.shulker.config

import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.StringWriter

abstract class CBungeeConfig : CConfig<Configuration>(BungeeYamlConfigurationWrapper())

/**
 * Note: headers are not available in the Bungee config API, so ShulkerFramework uses its own method
 */
class BungeeYamlConfigurationWrapper : YamlConfigurationWrapper<Configuration>() {
    private val yamlProvider: ConfigurationProvider = ConfigurationProvider.getProvider(YamlConfiguration::class.java)
    private var header: String? = null

    override fun loadFromFile(file: File): Configuration = yamlProvider.load(file)

    override fun saveToFile(configuration: Configuration, file: File) {
        val sw = StringWriter()
        yamlProvider.save(configuration, sw)
        file.writeText(
            // eventual header, then the actual contents
            (header?.let { it + System.lineSeparator() } ?: "") + sw.toString()
        )
        yamlProvider.save(configuration, file)
    }

    override fun newConfiguration(): Configuration = yamlProvider.load("")

    override fun contains(configuration: Configuration, path: String) = configuration.contains(path)

    override fun set(configuration: Configuration, path: String, value: Any?) {
        configuration.set(path, value)
    }

    override fun setHeader(configuration: Configuration, header: String) {
        this.header = header
    }

    override fun getValues(configuration: Configuration): Map<String, Any?> {
        val selfField = configuration.javaClass.getDeclaredField("self")
        selfField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return selfField.get(configuration) as Map<String, Any?>
    }

    @Throws(ClassCastException::class)
    override fun <A> get(configuration: Configuration, path: String): A? {
        @Suppress("UNCHECKED_CAST")
        return getAny(configuration, path) as A?
    }

    override fun getAny(configuration: Configuration, path: String): Any? = configuration.get(path)
}
