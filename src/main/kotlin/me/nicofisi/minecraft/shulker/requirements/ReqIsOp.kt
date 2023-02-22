package me.nicofisi.minecraft.shulker.requirements

import me.nicofisi.minecraft.shulker.commands.CCommand
import me.nicofisi.minecraft.shulker.commands.CRequirement
import me.nicofisi.minecraft.shulker.utils.colored
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ReqIsOp : CRequirement() {
    override fun check(sender: CommandSender, command: CCommand?): Boolean = sender.isOp

    override fun createErrorMessage(sender: CommandSender?, action: String): String =
        "&pYou need to be a server operator to &p$action".colored
}
