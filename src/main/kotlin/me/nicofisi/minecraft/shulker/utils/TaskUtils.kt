package me.nicofisi.minecraft.shulker.utils

import me.nicofisi.minecraft.shulker.PluginInfo
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import java.util.concurrent.TimeUnit

fun runTask(task: () -> Unit): CTask {
    if (isBungee) throw UnsupportedOperationException(
        "Only runTaskAsync, runTaskLaterAsync and runTaskTimerAsync are supported on BungeeCord"
    )

    return Bukkit.getScheduler().runTask(PluginInfo.Spigot.plugin, enhanceTask(task))
        .run { CTask(this) }
}

fun runTaskAsync(task: () -> Unit): CTask {
    return if (isBungee)
        ProxyServer.getInstance().scheduler.runAsync(PluginInfo.Bungee.plugin, enhanceTask(task))
            .run { CTask(this) }
    else
        Bukkit.getScheduler().runTaskAsynchronously(PluginInfo.Spigot.plugin, enhanceTask(task))
            .run { CTask(this) }

}

fun runTaskLater(delay: Duration, task: () -> Unit): CTask {
    if (isBungee) throw UnsupportedOperationException(
        "Only runTaskAsync, runTaskLaterAsync and runTaskTimerAsync are supported on BungeeCord"
    )

    return Bukkit.getScheduler().runTaskLater(PluginInfo.Spigot.plugin, enhanceTask(task), delay.toMillis() / 50)
        .run { CTask(this) }
}

fun runTaskLaterAsync(delay: Duration, task: () -> Unit): CTask {
    return if (isBungee)
        ProxyServer.getInstance().scheduler.schedule(
            PluginInfo.Bungee.plugin, enhanceTask(task), delay.toMillis(), TimeUnit.MILLISECONDS
        )
            .run { CTask(this) }
    else
        Bukkit.getScheduler()
            .runTaskLaterAsynchronously(PluginInfo.Spigot.plugin, enhanceTask(task), delay.toMillis() / 50)
            .run { CTask(this) }
}

fun runTaskTimer(delayToFirst: Duration, delayBetween: Duration, task: () -> Unit): CTask {
    if (isBungee) throw UnsupportedOperationException(
        "Only runTaskAsync, runTaskLaterAsync and runTaskTimerAsync are supported on BungeeCord"
    )

    return Bukkit.getScheduler().runTaskTimer(
        PluginInfo.Spigot.plugin, enhanceTask(task), delayToFirst.toMillis() / 50, delayBetween.toMillis() / 50
    )
        .run { CTask(this) }
}

fun runTaskTimerAsync(delayToFirst: Duration, delayBetween: Duration, task: () -> Unit): CTask {
    return if (isBungee)
        ProxyServer.getInstance().scheduler.schedule(
            PluginInfo.Bungee.plugin,
            enhanceTask(task),
            delayToFirst.toMillis(),
            delayBetween.toMillis(),
            TimeUnit.MILLISECONDS
        )
            .run { CTask(this) }
    else
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            PluginInfo.Spigot.plugin, enhanceTask(task), delayToFirst.toMillis() / 50, delayBetween.toMillis() / 50
        )
            .run { CTask(this) }
}


data class CTask(val id: Int) {
    constructor(task: BukkitTask) : this(task.taskId)
    constructor(task: ScheduledTask) : this(task.id)

    fun cancel() =
        if (isBungee)
            ProxyServer.getInstance().scheduler.cancel(id)
        else
            Bukkit.getScheduler().cancelTask(id)
}

/**
 * Currently simply runs the task with no enhancements.
 * Will possibly we edited if we decide to catch exceptions here on our own for some reason.
 */
private fun enhanceTask(task: () -> Unit) = task
