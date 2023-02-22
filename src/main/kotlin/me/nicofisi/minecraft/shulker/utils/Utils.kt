package me.nicofisi.minecraft.shulker.utils

import me.nicofisi.minecraft.shulker.PluginInfo
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Integer.max
import java.nio.file.Paths
import java.time.Duration
import java.util.logging.Logger
import kotlin.random.Random

fun randomErrorPrefix(): String = "&p[" + listOf(
    "Oh no!", "How unfortunate", "Oops", ":(", "Well"
//    "[///]"
).random() + "] &s"

val isBungee = PluginInfo.isBungee()

val pluginJarFile = Paths.get(PluginInfo::class.java.protectionDomain.codeSource.location.toURI())

val dataFolder =
    (if (isBungee)
        PluginInfo.Bungee.plugin.dataFolder.toPath()
    else
        PluginInfo.Spigot.plugin.dataFolder.toPath())

val logger: Logger =
    if (isBungee)
        PluginInfo.Bungee.plugin.logger
    else
        PluginInfo.Spigot.plugin.logger

fun logInfo(message: String) = logger.info(message)

fun logWarning(message: String) = logger.warning(message)

fun logError(message: String) = logger.severe(message)


fun pluginExists(name: String): Boolean = Bukkit.getPluginManager().getPlugin(name) != null

fun isPluginEnabled(name: String): Boolean = Bukkit.getPluginManager().getPlugin(name)?.isEnabled == true


val String.colored: String
    get() =
        ChatColor.translateAlternateColorCodes(
            '&', replace(
                this, mapOf(
                    "<primary>" to "&p",
                    "<secondary>" to "&s",
                    "<accent>" to "&t",
                    "&p" to "&" + PluginInfo.primaryColor.toString(),
                    "&s" to "&" + PluginInfo.secondaryColor.toString(),
                    "&t" to "&" + PluginInfo.accentColor.toString(),
                    "&0" to "\u00A70",
                    "&1" to "\u00A71",
                    "&2" to "\u00A72",
                    "&3" to "\u00A73",
                    "&4" to "\u00A74",
                    "&5" to "\u00A75",
                    "&6" to "\u00A76",
                    "&7" to "\u00A77",
                    "&8" to "\u00A78",
                    "&9" to "\u00A79",
                    "&a" to "\u00A7a",
                    "&b" to "\u00A7b",
                    "&c" to "\u00A7c",
                    "&d" to "\u00A7d",
                    "&e" to "\u00A7e",
                    "&f" to "\u00A7f",
                    // Magic
                    "&m" to "\u00A7m", // strike through
                    "&n" to "\u00A7n", // underlined
                    "&l" to "\u00A7l", // bold
                    "&k" to "\u00A7k", // random
                    "&o" to "\u00A7o", // italics
                    "&r" to "\u00A7r", // reset (white in Minecraft, default color in console (ssh))
                    // Other style
                    "<black>" to "\u00A70",
                    "<navy>" to "\u00A71",
                    "<green>" to "\u00A72",
                    "<teal>" to "\u00A73",
                    "<red>" to "\u00A74",
                    "<purple>" to "\u00A75",
                    "<gold>" to "\u00A76",
                    "<orange>" to "\u00A76",
                    "<silver>" to "\u00A77",
                    "<gray>" to "\u00A78",
                    "<grey>" to "\u00A78",
                    "<blue>" to "\u00A79",
                    "<lime>" to "\u00A7a",
                    "<aqua>" to "\u00A7b",
                    "<rose>" to "\u00A7c",
                    "<pink>" to "\u00A7d",
                    "<yellow>" to "\u00A7e",
                    "<white>" to "\u00A7f",
                    "<magic>" to "\u00A7k",
                    "<bold>" to "\u00A7l",
                    "<strong>" to "\u00A7l",
                    "<strike>" to "\u00A7m",
                    "<crossed>" to "\u00A7m",
                    "<strikethrough>" to "\u00A7m",
                    "<under>" to "\u00A7n",
                    "<underline>" to "\u00A7n",
                    "<italic>" to "\u00A7o",
                    "<italics>" to "\u00A7o",
                    "<em>" to "\u00A7o",
                    "<reset>" to "\u00A7r"
                )
            )
        )

fun replace(string: String, replacers: Map<String, String>): String {
    var str = string
    replacers.forEach { (original, new) ->
        str = str.replace(original, new)
    }
    return str
}

val String.colorsRemoved: String
    get() =
        ChatColor.stripColor(this)!!


fun CommandSender.sendColored(message: String) = sendMessage(("&s$message").colored)

const val IDEAL_HEADER_LENGTH = 50

fun CommandSender.sendHeader(message: String) {
    val minSize = message.length + 4 * 2
    val leftToIdeal = max(0, IDEAL_HEADER_LENGTH - minSize)
    val border = "&s[".colored + "+".repeat(leftToIdeal / 2 + 1) + "]"
    sendMessage("$border $message $border")
}

val HARDCODED_ASTERISK = "ASTERISK_${Random.nextLong()}"

fun CommandSender.sendError(messagePath: String, vararg arguments: String) {
    var message = messagePath // TODO actually read from the path
    message = message.replace("\\%", HARDCODED_ASTERISK)

    arguments.withIndex().forEach { (index, argument) ->
        message = message.replace("%$index", argument)
    }
    message = message.replace(HARDCODED_ASTERISK, "%")
//    sendMessage(randomErrorPrefix().colored + message)
    sendMessage(message)
}

//fun CommandSender.sendHardcodedError(message: String) = sendMessage(randomErrorPrefix().colored + message)
/**
 * Currently it's no different than just [org.bukkit.command.CommandSender.sendMessage],
 * as it doesn't include any prefix, as it used to.
 */
fun CommandSender.sendHardcodedError(message: String) = sendMessage(message)

fun CommandSender?.getCommandPrefix() = if (this == null || this is Player) "/" else ""


fun OfflinePlayer.toOnline(): Player? = Bukkit.getPlayerExact(name!!)


/*
 * Maybe a long param and a require()?
 * I need to make this a Duration.Companion.ofTicks() when Kotlin 1.3 is out
 * EDIT after 1.13: well, it only works for classes that have a companion object already defined in Kotlin
 */
fun durationOfTicks(ticks: Int): Duration = Duration.ofMillis(ticks.toLong() * 50)
