package me.nicofisi.minecraft.shulker.requirements

import me.nicofisi.minecraft.shulker.utils.colored
import me.nicofisi.minecraft.shulker.commands.CCommand
import me.nicofisi.minecraft.shulker.commands.CRequirement
import org.bukkit.command.CommandSender

data class ReqHasPermission(val permission: String) : CRequirement() {
    override fun check(sender: CommandSender, command: CCommand?): Boolean = sender.hasPermission(permission)

    override fun createErrorMessage(sender: CommandSender, action: String): String =
            "&sYou need the &p$permission &spermission to &p$action".colored
}
