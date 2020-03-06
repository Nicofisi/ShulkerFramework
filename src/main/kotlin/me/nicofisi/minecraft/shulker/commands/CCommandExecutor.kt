package me.nicofisi.minecraft.shulker.commands

import arrow.core.Either
import me.nicofisi.minecraft.shulker.PluginInfo
import me.nicofisi.minecraft.shulker.utils.runTaskAsync
import me.nicofisi.minecraft.shulker.utils.sendError
import me.nicofisi.minecraft.shulker.utils.sendHardcodedError
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

open class CCommandExecutor(vararg val commands: CCommand) {

    fun registerCommands() {
        val commandMap = Bukkit.getServer().commandMap
        commands.forEach { cc ->
            val command = object : Command(cc.aliases.first()) {
                init {
                    this.description = cc.helpDescription
                    this.usageMessage = "TODO" // TODO
                    this.aliases = cc.aliases
                }

                override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                    handleCCommand(sender, cc, label, args.toList())
                    return true
                }
            }
            commandMap.register(PluginInfo.Spigot.plugin.name.toLowerCase(), command)
        }
    }

    companion object {
        fun handleCCommand(sender: CommandSender, cc: CCommand, label: String, args: List<String>) {
            fun doHandle() {
                cc.requirements.forEach { it.validate(sender, cc) }

                val argsAfterJoin = {
                    val tempArgs = args.take(cc.arguments.size).toMutableList()
                    if (args.size >= cc.arguments.size) {
                        tempArgs.add(args.drop(cc.arguments.size).joinToString(" "))
                    }
                    tempArgs
                }()

                val parsedArgs = CParsedArguments(cc.arguments.withIndex().map { (index, cArg) ->
                    val arg = argsAfterJoin.getOrNull(index)
                    if (arg != null) {
                        when (val parsed = cArg.cType.parse(arg)) {
                            is Either.Left -> parsed.a // the parsed value
                            is Either.Right -> {
                                sender.sendHardcodedError(parsed.b) // the parse fail reason
                                return@doHandle
                            }
                        }
                    } else {
                        val value = cArg.defValue(sender)?.first
                        if (value == null && cArg.isRequired) {
                            sender.sendError("")
                            return@doHandle
                        }
                        value
                    }
                })

                val runAsync = cc.javaClass.isAnnotationPresent(ExecuteAsync::class.java)
                val exec = { cc.execute(sender, parsedArgs, ExecutionExtras(label)) }
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
                sender.sendError("An error has occurred while attempting to perform your command")
                if (sender !is ConsoleCommandSender) {
                    sender.sendError(
                            "The error: &p${ex.javaClass.canonicalName}" +
                                    if (ex.message == null) "" else ": " + ex.message)
                }
                ex.printStackTrace()
            }
        }
    }
}
