package me.nicofisi.minecraft.shulker.commands

import me.nicofisi.minecraft.shulker.utils.colored
import me.nicofisi.minecraft.shulker.utils.sendColored
import org.bukkit.command.CommandSender

interface CCommand {
    val aliases: List<String>

    val helpDescription: String

    val arguments: List<CArgument<Any>>

    val requirements: List<CRequirement>

    fun execute(sender: CommandSender, args: CParsedArguments): Unit = execute(sender)

    fun execute(sender: CommandSender): Unit = throw NotImplementedError()
}

object CCommandWithArgs {
    operator fun invoke(cmdAliases: List<String>, helpDesc: String,
                        reqs: List<CRequirement>, cmdArgs: List<CArgument<Any>>,
                        onExecute: (CommandSender, CParsedArguments) -> Unit): CCommand = object : CCommand {
        override val aliases = cmdAliases
        override val helpDescription = helpDesc
        override val arguments = cmdArgs
        override val requirements = reqs

        override fun execute(sender: CommandSender, args: CParsedArguments) = onExecute(sender, args)
    }
}

abstract class CCommandAbstract : CCommand {
    override val arguments = emptyList<CArgument<Any>>()
    override val requirements = emptyList<CRequirement>()
}

object CCommandNoArgs {
    operator fun invoke(cmdAliases: List<String>, helpDesc: String, reqs: List<CRequirement> = emptyList(),
                        onExecute: (CommandSender) -> Unit): CCommand = object : CCommand {
        override val aliases = cmdAliases
        override val helpDescription = helpDesc
        override val arguments = emptyList<CArgument<Any>>()
        override val requirements = reqs

        override fun execute(sender: CommandSender) = onExecute(sender)
    }
}

object CParentCommand {
    operator fun invoke(cmdAliases: List<String>, helpDesc: String,
                        vararg children: CCommand): CCommand = object : CCommand {
        @Suppress("UnnecessaryVariable") // an IntelliJ bug
        override val aliases = cmdAliases
        override val helpDescription = helpDesc
        override val arguments = listOf(
                CArgument(StringType, "subcommand", defValue = { Pair("help", "help") }),
                CArgument(StringType, "arguments", isRequired = false))
        override val requirements = emptyList<CRequirement>()

        // TODO multiple help pages
        override fun execute(sender: CommandSender, args: CParsedArguments) {
            fun showHelp(sender: CommandSender, unknownAlias: String? = null) {
                if (unknownAlias != null) { // TODO label
                    sender.sendColored("Command &p/...".colored + unknownAlias + "&scould not be found".colored)
                }
                sender.sendColored("Help for &p/... ${aliases.first()}") // TODO label
                children.forEach {
                    // TODO label
                    sender.sendColored("/... ${aliases.first()} &p${it.aliases.first()} &s- ${it.helpDescription}")
                }
            }

            val argAlias = (args[0] as String).toLowerCase()

            if (argAlias == "help" || argAlias == "?") {
                showHelp(sender)
            } else {
                val argArgs = (args[1] as String?)?.split(" ") ?: emptyList()

                val child = children.find { it.aliases.contains(argAlias) }
                if (child != null) {
                    sender.sendColored(args[1] as String? + " | " + argAlias.length + " | " + argArgs.joinToString { ", " })
                    argArgs.forEach {
                        sender.sendMessage(it)
                    }
                    CCommandExecutor.handleCCommand(sender, child, argAlias, argArgs)
                } else {
                    showHelp(sender, argAlias)
                }
            }
        }
    }
}
