package me.nicofisi.minecraft.shulker.commands

import me.nicofisi.minecraft.shulker.PluginInfo

object CDebugCommandExecutor : CCommandExecutor(
    CParentCommand(
        listOf(PluginInfo.Spigot.plugin.name.replace(Regex("[^a-zA-Z0-9]"), "") + "debug"),
        "the debug command for ${PluginInfo.Spigot.plugin.name}"
        // TODO
    )
)
