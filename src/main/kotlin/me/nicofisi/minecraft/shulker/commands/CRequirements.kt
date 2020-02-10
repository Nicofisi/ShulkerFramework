package me.nicofisi.minecraft.shulker.commands

import me.nicofisi.minecraft.shulker.utils.sendHardcodedError
import org.bukkit.command.CommandSender

abstract class CRequirement {
    abstract fun check(sender: CommandSender, command: CCommand?): Boolean

    abstract fun createErrorMessage(sender: CommandSender, action: String): String

    open fun createErrorMessage(sender: CommandSender, command: CCommand? = null): String =
            createErrorMessage(sender, getAction(command))

    open fun getAction(command: CCommand? = null) =
            if (command == null) "perform this action" else "execute this command"

    fun sendErrorMessage(sender: CommandSender, command: CCommand? = null): Unit =
            sender.sendHardcodedError(createErrorMessage(sender, command))

    fun sendErrorMessage(sender: CommandSender, action: String): Unit =
            sender.sendHardcodedError(createErrorMessage(sender, action))

    fun validate(sender: CommandSender, command: CCommand? = null) {
        if (!check(sender, command)) {
            throw CommonRequirementException(this, getAction(command))
        }
    }

    fun validate(sender: CommandSender, message: String) {
        if (!check(sender, null)) {
            throw CommonRequirementException(this, message)
        }
    }
}

class CommonRequirementException(val requirement: CRequirement,
                                 override val message: String) : RuntimeException(message)
