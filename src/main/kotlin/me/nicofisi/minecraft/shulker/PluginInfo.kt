package me.nicofisi.minecraft.shulker

import net.md_5.bungee.api.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object PluginInfo {
    private var wasInit = false

    private var isBungee: Boolean? = null

    fun isBungee() = isBungee!! // errors on purpose, TODO doc

    var primaryColor: Char = '0'
        private set

    var secondaryColor: Char = '1'
        private set

    object Spigot {
        lateinit var plugin: JavaPlugin
            private set

        fun init(plugin: JavaPlugin, primaryColor: Char, secondaryColor: Char) {
            require(!wasInit) { "The PluginInfo class has already been initialized" }

            Spigot.plugin = plugin
            PluginInfo.primaryColor = primaryColor
            PluginInfo.secondaryColor = secondaryColor

            wasInit = true
            isBungee = false
        }
    }

    object Bungee {
        lateinit var plugin: Plugin
            private set

        fun init(plugin: Plugin, primaryColor: Char, secondaryColor: Char) {
            require(!wasInit) { "The PluginInfo class has already been initialized" }

            Bungee.plugin = plugin
            PluginInfo.primaryColor = primaryColor
            PluginInfo.secondaryColor = secondaryColor

            wasInit = true
            isBungee = true
        }
    }
}
