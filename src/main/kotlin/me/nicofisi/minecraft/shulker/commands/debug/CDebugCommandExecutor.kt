package me.nicofisi.minecraft.shulker.commands.debug

import me.nicofisi.minecraft.shulker.PluginInfo
import me.nicofisi.minecraft.shulker.commands.CCommandExecutor
import me.nicofisi.minecraft.shulker.commands.CParentCommand

@Suppress("unused")
object CDebugCommandExecutor : CCommandExecutor(
    CParentCommand(
        listOf(PluginInfo.Spigot.plugin.name.replace(Regex("[^a-zA-Z0-9]"), "") + "debug"),
        "the debug command for ${PluginInfo.Spigot.plugin.name}",
        CDebugSubcommandError,
        CDebugSubcommandArguments
    )
)
