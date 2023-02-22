package me.nicofisi.minecraft.shulker.commands.debug

import me.nicofisi.minecraft.shulker.commands.*
import me.nicofisi.minecraft.shulker.requirements.ReqIsOp
import me.nicofisi.minecraft.shulker.utils.colored
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

object CDebugSubcommandArguments : CCommandAbstract() {
    override val aliases = listOf("arguments", "args")
    override val helpDescription = "test parsing of different argument types"
    override val requirements = listOf(
        ReqIsOp
    )
    override val arguments = listOf(
        CArgument(BooleanType, "boolean", defValue = { DefaultArgumentValue(true, "true") }),
        CArgument(GameModeType, "gamemode", defValue = { DefaultArgumentValue(GameMode.SURVIVAL, "survival") }),
        CArgument(IntegerType, "integer", defValue = { DefaultArgumentValue(0, "0") }),
        CArgument(LongType, "long", defValue = { DefaultArgumentValue(0L, "0L") }),
        CArgument(DoubleType, "double", defValue = { DefaultArgumentValue(0.0, "0.0") }),
        CArgument(OfflinePlayerType, "offlineplayer", defValue = {
            Bukkit.getOnlinePlayers().firstOrNull()?.let {
                DefaultArgumentValue(it, it.name)
            }
        }),
        CArgument(PlayerType, "player", defValue = {
            Bukkit.getOnlinePlayers().firstOrNull()?.let {
                DefaultArgumentValue(it, it.name)
            }
        }),
        CArgument(StringType, "string", defValue = { DefaultArgumentValue("hello", "hello") }),
        CArgument(WorldType, "world", defValue = {
            Bukkit.getWorlds().firstOrNull()?.let {
                DefaultArgumentValue(it, it.name)
            }
        })
    )

    override fun execute(sender: CommandSender, args: CParsedArguments, extras: ExecutionExtras) {
        sender.sendMessage("&sboolean is: &p".colored + args[0])
        sender.sendMessage("&sgamemode is: &p".colored + args[1])
        sender.sendMessage("&sinteger is: &p".colored + args[2])
        sender.sendMessage("&slong is: &p".colored + args[3])
        sender.sendMessage("&sdouble is: &p".colored + args[4])
        sender.sendMessage("&sofflineplayer is: &p".colored + args[5])
        sender.sendMessage("&splayer is: &p".colored + args[6])
        sender.sendMessage("&sstring is: &p".colored + args[7])
        sender.sendMessage("&sworld is: &p".colored + args[8])
    }
}
