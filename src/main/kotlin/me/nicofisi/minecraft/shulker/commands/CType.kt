package me.nicofisi.minecraft.shulker.commands

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import me.nicofisi.minecraft.shulker.utils.colored
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import java.math.BigInteger
import java.util.*

typealias ParseFailReason = String
typealias ParseResult<A> = Either<A, ParseFailReason>

interface CType<A> {
    fun parse(string: String): ParseResult<A>

    /**
     * Returns a list of suggestions to send to the player when they press the tab key with the given
     * string typed, in a place where an object of this type is expected
     *
     * @param mustStartWith the string; implementations are allowed to return an empty list when
     *                      the length of this string is lower than some given small number
     */
    fun tabSuggestions(mustStartWith: String): List<String> = emptyList()
}

object BooleanType : CType<Boolean> {
    override fun parse(string: String): ParseResult<Boolean> = when (string) {
        "yes", "true", "y", "t" -> Left(true)
        "no", "false", "n", "f" -> Left(false)
        else -> Right("&sYou typed &p$string in a place where only &pyes &sor &pno &sshould be used".colored)
    }
}

object GameModeType : CType<GameMode> {
    override fun parse(string: String): ParseResult<GameMode> = when (string) {
        "0", "survival", "s" -> Left(GameMode.SURVIVAL)
        "1", "creative", "c" -> Left(GameMode.CREATIVE)
        "2", "adventure", "a" -> Left(GameMode.ADVENTURE)
        "3", "spectator", "sp" -> Left(GameMode.SPECTATOR)
        else -> Right("&sGame mode called &p$string &sdoesn't exist".colored)
    }

    override fun tabSuggestions(mustStartWith: String) =
        listOf("survival", "creative", "adventure", "spectator")
            .filter { it.startsWith(mustStartWith.toLowerCase()) }
}

object IntegerType : CType<Int> {
    override fun parse(string: String): ParseResult<Int> {
        return try {
            Left(string.toInt())
        } catch (ex: NumberFormatException) {
            try {
                BigInteger(string)
                Right("&sThe number &p$string &sis too big, it should be at most ${Int.MAX_VALUE}".colored)
            } catch (ex: NumberFormatException) {
                Right("&sThe text &p$string &sis not an integer".colored)
            }
        }
    }
}

object LongType : CType<Long> {
    override fun parse(string: String): ParseResult<Long> {
        return try {
            Left(string.toLong())
        } catch (ex: NumberFormatException) {
            try {
                BigInteger(string)
                Right("&sThe number &p$string &sis too big, it should be at most ${Long.MAX_VALUE}".colored)
            } catch (ex: NumberFormatException) {
                Right("&sThe text &p$string &sis not an integer".colored)
            }
        }
    }
}

object DoubleType : CType<Double> {
    override fun parse(string: String): ParseResult<Double> {
        return try {
            Left(string.toDouble())
        } catch (ex: NumberFormatException) {
            Right("&sThe text &p$string is not a floating-point number".colored)
        }
    }
}

// TODO ExistingOfflinePlayerType and AnyOfflinePlayerTpe
object OfflinePlayerType : CType<OfflinePlayer> {
    override fun parse(string: String): ParseResult<OfflinePlayer> {
        @Suppress("DEPRECATION") // needed here
        return Bukkit.getOfflinePlayer(string)?.let { Left(it) }
            ?: try {
                Left(Bukkit.getOfflinePlayer(UUID.fromString(string)))
            } catch (ex: IllegalArgumentException) {
                Right("&sNo player named &p$string &scould be found".colored)
            }
    }

    override fun tabSuggestions(mustStartWith: String) = PlayerType.tabSuggestions(mustStartWith)

    // TODO find a way faster way
    // than Bukkit.getOfflinePlayers.asScala.map(_.getName).filter(_.startsWith(mustStartWith))
}

object PlayerType : CType<Player> {
    override fun parse(string: String): ParseResult<Player> {
        return Bukkit.getPlayerExact(string)?.let { Left(it) }
            ?: try {
                Left(Bukkit.getPlayer(UUID.fromString(string)) ?: throw RuntimeException())
            } catch (ex: Exception) { // IllegalArgumentException from UUID.fromString, or RuntimeException from above
                val players = Bukkit.getOnlinePlayers().filter { it.name.startsWith(string, ignoreCase = true) }
                when {
                    players.isEmpty() ->
                        Right("&sNo online player could be found whose name starts with &p$string".colored)
                    players.size > 1 ->
                        Right("&sThere are currently multiple players online whose names start with &p$string".colored)
                    else ->
                        Left(players.first())
                }
            }
    }
}

object StringType : CType<String> {
    override fun parse(string: String) = Left(string)
}

object WorldType : CType<World> {
    override fun parse(string: String): ParseResult<World> {
        Bukkit.getWorld(string)?.let { return Left(it) }

        val worlds = Bukkit.getWorlds().filter { it.name.startsWith(string, ignoreCase = true) }
        return when {
            worlds.isEmpty() ->
                Right("&sThere aren't any loaded worlds whose names start with &p$string".colored)
            worlds.size > 1 ->
                Right("&sThere are currently a few loaded worlds whose names start with &p$string".colored)
            else ->
                Left(worlds.first())
        }
    }

    override fun tabSuggestions(mustStartWith: String) =
        Bukkit.getWorlds().map { it.name }.filter { it.startsWith(mustStartWith, ignoreCase = true) }
}
