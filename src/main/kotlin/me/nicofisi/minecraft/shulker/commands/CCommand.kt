package me.nicofisi.minecraft.shulker.commands

import me.nicofisi.minecraft.shulker.utils.colored
import me.nicofisi.minecraft.shulker.utils.getCommandPrefix
import me.nicofisi.minecraft.shulker.utils.sendHeader
import org.bukkit.command.CommandSender

interface CCommand {
    val aliases: List<String>

    val helpDescription: String

    val arguments: List<CArgument<Any>>

    val requirements: List<CRequirement>

    fun execute(sender: CommandSender, args: CParsedArguments, extras: ExecutionExtras): Unit = execute(sender)

    fun execute(sender: CommandSender): Unit = throw NotImplementedError()

    fun getHelpForArguments(sender: CommandSender?, argumentIndexesToHighlight: List<Int>): String {
        return arguments.map { arg ->
            val defValue = arg.defValue(sender)
            when {
                defValue != null -> {
                    "[" + arg.name + "=" + defValue.textToDisplay + "]"
                }

                arg.isRequired -> {
                    "<" + arg.name + ">"
                }

                else -> {
                    "[" + arg.name + "]"
                }
            }
        }.withIndex().joinToString(" ") { (index, argHelp) ->
            (if (argumentIndexesToHighlight.contains(index)) "&t" else "&p").colored + argHelp
        }
    }

    fun getCorrectUsage(
        sender: CommandSender?,
        labelList: List<String>,
        argumentIndexesToHighlight: List<Int>
    ): String {
        return "&p".colored +
                sender.getCommandPrefix() +
                labelList.joinToString(" ") +
                " " +
                getHelpForArguments(sender, argumentIndexesToHighlight)
    }

    fun sendCorrectUsage(sender: CommandSender, labelList: List<String>, argumentIndexesToHighlight: List<Int>) {
        sender.sendMessage("&sCorrect usage: ".colored + getCorrectUsage(sender, labelList, argumentIndexesToHighlight))
    }
}

data class ExecutionExtras(val labelList: List<String>) {
    val label = labelList.joinToString(" ")
}

object CCommandWithArgs {
    operator fun invoke(
        cmdAliases: List<String>, helpDesc: String,
        reqs: List<CRequirement>, cmdArgs: List<CArgument<Any>>,
        onExecute: (CommandSender, CParsedArguments) -> Unit
    ): CCommand = object : CCommand {
        override val aliases = cmdAliases
        override val helpDescription = helpDesc
        override val arguments = cmdArgs
        override val requirements = reqs

        override fun execute(sender: CommandSender, args: CParsedArguments, extras: ExecutionExtras) =
            onExecute(sender, args)
    }
}

abstract class CCommandAbstract : CCommand {
    override val arguments = emptyList<CArgument<Any>>()
    override val requirements = emptyList<CRequirement>()
}

object CCommandNoArgs {
    operator fun invoke(
        cmdAliases: List<String>, helpDesc: String, reqs: List<CRequirement> = emptyList(),
        onExecute: (CommandSender) -> Unit
    ): CCommand = object : CCommand {
        override val aliases = cmdAliases
        override val helpDescription = helpDesc
        override val arguments = emptyList<CArgument<Any>>()
        override val requirements = reqs

        override fun execute(sender: CommandSender) = onExecute(sender)
    }
}

class CParentCommand(
    cmdAliases: List<String>,
    helpDesc: String,
    vararg val children: CCommand
) : CCommand {
    @Suppress("UnnecessaryVariable") // an IntelliJ bug
    override val aliases = cmdAliases
    override val helpDescription = helpDesc
    override val arguments = listOf(
        CArgument(StringType, "subcommand", defValue = { DefaultArgumentValue("help", "help") }),
        CArgument(StringType, "arguments", isRequired = false)
    )
    override val requirements = emptyList<CRequirement>()

    // TODO multiple help pages
    override fun execute(sender: CommandSender, args: CParsedArguments, extras: ExecutionExtras) {

        /**
         * @param unknownSubcommand If showing help is triggered by stumbling upon an unknown subcommand argument,
         * an error line is sent first, explaining what happened
         */
        fun showHelp(sender: CommandSender, unknownSubcommand: String? = null) {
            if (unknownSubcommand != null) {
                sender.sendMessage("&sCommand &p/${extras.label} &t".colored + unknownSubcommand + " &scould not be found".colored)
            }
            sender.sendHeader("Help for &p".colored + sender.getCommandPrefix() + extras.label)
            if (children.isEmpty()) {
                sender.sendMessage("&s&o".colored + "This command has no subcommands")
            }
            children.forEach { child ->
                /*
                 * Examples:
                 *   /nav location <x> <y> <z> - does something
                 *   navigate stop [player=Notch] - does something else
                 */
                sender.sendMessage(
                    "&s".colored +
                            sender.getCommandPrefix() +
                            extras.label +
                            " &t".colored +
                            child.aliases.first() +
                            " " +
                            child.getHelpForArguments(sender, argumentIndexesToHighlight = emptyList()).let {
                                if (it.isEmpty()) it else "$it "
                            } +
                            "&s- ".colored +
                            child.helpDescription
                )
            }
        }

        val argAlias = (args[0] as String).toLowerCase()

        if (argAlias == "help" || argAlias == "?") {
            showHelp(sender)
        } else {
            val child = findChild(argAlias)
            if (child != null) {
                val argArgs = (args[1] as String?)?.split(" ") ?: emptyList()

                CCommandExecutor.handleCCommand(sender, child, extras.labelList + argAlias, argArgs)
            } else {
                showHelp(sender, argAlias)
            }
        }
    }

    fun findChild(alias: String): CCommand? {
        val lowerAlias = alias.toLowerCase()

        return children.find { it.aliases.contains(lowerAlias) }
    }
}
