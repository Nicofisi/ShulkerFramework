package me.nicofisi.minecraft.shulker.commands

import arrow.core.Either
import me.nicofisi.minecraft.shulker.PluginInfo
import me.nicofisi.minecraft.shulker.requirements.ReqHasPermission
import me.nicofisi.minecraft.shulker.utils.colored
import me.nicofisi.minecraft.shulker.utils.runTaskAsync
import me.nicofisi.minecraft.shulker.utils.sendError
import me.nicofisi.minecraft.shulker.utils.sendHardcodedError
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

open class CCommandExecutor(vararg val commands: CCommand) {

    @Suppress("unused")
    fun registerCommands() {
        val commandMap = Bukkit.getServer().commandMap
        commands.forEach { cc ->
            val command = object : Command(cc.aliases.first()) {
                init {
                    val firstPermissionRequirement = cc.requirements
                        .filterIsInstance(ReqHasPermission::class.java).firstOrNull()
                    this.description = cc.helpDescription
                    this.usageMessage = cc.getCorrectUsage(null, listOf(cc.aliases.first()), emptyList())
                    this.aliases = cc.aliases
                    this.permission = firstPermissionRequirement?.permission
                    this.permissionMessage = firstPermissionRequirement
                        ?.createErrorMessage(null, cc)
                }

                override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                    handleCCommand(sender, cc, listOf(commandLabel), args.toList())
                    return true
                }

                override fun tabComplete(
                    sender: CommandSender,
                    alias: String,
                    args: Array<out String>
                ): MutableList<String> {
                    return tabComplete(sender, cc, listOf(alias), args.toList()).toMutableList()
                }

                override fun tabComplete(
                    sender: CommandSender,
                    alias: String,
                    args: Array<out String>,
                    targetLocation: Location?
                ): MutableList<String> {
                    return tabComplete(
                        sender, cc, listOf(alias), args.toList(), targetLocation
                    ).toMutableList()
                }
            }
            commandMap.register(PluginInfo.Spigot.plugin.name.lowercase(), command)
        }
    }

    companion object {
        fun tabComplete(
            sender: CommandSender,
            cc: CCommand,
            labelList: List<String>,
            args: List<String>,
            targetLocation: Location? = null
        ): List<String> {
            println("sender = [${sender}], cc = [${cc}], labelList = [${labelList}], args = [${args}]")
            if (cc is CParentCommand) {
                val lowerFirstArg = args[0].lowercase()
                return if (args.size < 2) {
                    return cc.children.flatMap { it.aliases }.filter { it.startsWith(lowerFirstArg) }
                } else {
                    cc.findChild(lowerFirstArg)?.let {
                        tabComplete(sender, it, labelList + lowerFirstArg, args.drop(1), targetLocation)
                    } ?: emptyList()
                }
            }
            return cc.arguments[args.size - 1].tabCompletions(args.last(), targetLocation)
        }

        fun handleCCommand(sender: CommandSender, cc: CCommand, labelList: List<String>, args: List<String>) {
            fun doHandle() {
                cc.requirements.forEach { it.validate(sender, cc) }

                if (cc.arguments.isEmpty() && args.isNotEmpty()) {
                    sender.sendError(
                        "&sThis command doesn't take any arguments, so the &p".colored +
                                args.joinToString(" ") + " &sthat you typed shouldn't be there".colored
                    )
                    cc.sendCorrectUsage(sender, labelList, emptyList())
                    return
                }

                val argsAfterJoin =
                    if (cc.arguments.isEmpty() || args.isEmpty())
                        emptyList()
                    else args.take(cc.arguments.size - 1) +
                            if (args.size >= cc.arguments.size)
                                listOf(args.drop(cc.arguments.size - 1).joinToString(" "))
                            else
                                emptyList()

                val parsedArgs = CParsedArguments(cc.arguments.withIndex().map { (argIndex, cArg) ->
                    val arg = argsAfterJoin.getOrNull(argIndex)
                    if (arg != null) {
                        when (val parsed = cArg.cType.parse(arg)) {
                            is Either.Left -> parsed.a // the parsed value
                            is Either.Right -> {
                                sender.sendHardcodedError(parsed.b) // the parse fail reason
                                cc.sendCorrectUsage(sender, labelList, argumentIndexesToHighlight = listOf(argIndex))
                                return@doHandle
                            }
                        }
                    } else {
                        val value = cArg.defValue(sender)?.value
                        if (value == null && cArg.isRequired) {
                            sender.sendError("&sThe argument &p${cArg.name} &sis required".colored)
                            cc.sendCorrectUsage(sender, labelList, argumentIndexesToHighlight = listOf(argIndex))
                            return@doHandle
                        }
                        value
                    }
                })

                val runAsync = cc.javaClass.isAnnotationPresent(ExecuteAsync::class.java)
                val exec = { cc.execute(sender, parsedArgs, ExecutionExtras(labelList)) }
                if (runAsync) {
                    runTaskAsync { exec() }
                } else {
                    exec()
                }
            }

            try {
                doHandle()
            } catch (ex: CommonRequirementException) {
                ex.requirement.sendErrorMessage(sender, ex.message)
            } catch (ex: Throwable) {
                sender.sendError("&sAn error has occurred while attempting to perform your command".colored)
                if (sender !is ConsoleCommandSender) {
                    sender.sendError(
                        "&sThe error: &p${ex.javaClass.canonicalName}".colored +
                                if (ex.message == null) "" else ": " + ex.message
                    )
                }
                ex.printStackTrace()
            }
        }
    }
}
