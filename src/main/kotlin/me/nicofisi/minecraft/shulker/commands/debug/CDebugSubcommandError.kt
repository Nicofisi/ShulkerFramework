package me.nicofisi.minecraft.shulker.commands.debug

import me.nicofisi.minecraft.shulker.commands.CCommandAbstract
import me.nicofisi.minecraft.shulker.commands.CParsedArguments
import me.nicofisi.minecraft.shulker.commands.ExecutionExtras
import me.nicofisi.minecraft.shulker.requirements.ReqIsOp
import org.bukkit.command.CommandSender

object CDebugSubcommandError : CCommandAbstract() {
    override val aliases = listOf("error", "exception")
    override val helpDescription = "throw an exception on purpose"
    override val requirements = listOf(
        ReqIsOp
    )

    override fun execute(sender: CommandSender, args: CParsedArguments, extras: ExecutionExtras) {
        throw ThrownOnPurposeException("This is an example exception, thrown on purpose by yourself")
    }
}

class ThrownOnPurposeException(message: String) : Exception(message)
