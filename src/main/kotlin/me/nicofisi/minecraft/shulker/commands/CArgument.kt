package me.nicofisi.minecraft.shulker.commands

import org.bukkit.Location
import org.bukkit.command.CommandSender

data class CArgument<out A>(
    val cType: CType<A>,
    val name: String,
    val defValue: ((CommandSender?) -> DefaultArgumentValue<A>?) = { null },
    val isRequired: Boolean = true,
    val tabCompletions: ((String, Location?) -> List<String>) =
        { mustStartWith, _ -> cType.tabCompletions(mustStartWith) }
)

data class DefaultArgumentValue<out A>(val value: A, val textToDisplay: String)
