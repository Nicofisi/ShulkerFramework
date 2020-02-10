package me.nicofisi.minecraft.shulker.commands

import org.bukkit.command.CommandSender

data class CArgument<out A>(val cType: CType<out A>,
                        val name: String,
                        val defValue: ((CommandSender) -> Pair<A, String>?) = { null },
                        val isRequired: Boolean = true)
