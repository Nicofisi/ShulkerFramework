package me.nicofisi.minecraft.shulker.commands

data class CParsedArguments(val args: List<Any?>) {

    operator fun get(index: Int): Any? = if (args.size > index) args[index] else null

    fun isAnySpecified() = args.any { it != null && it != false }
}
