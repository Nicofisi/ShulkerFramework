package me.nicofisi.minecraft.shulker.config

import java.io.File
import java.lang.ClassCastException

abstract class YamlConfigurationWrapper<T> {
    abstract fun loadFromFile(file: File): T

    abstract fun saveToFile(configuration: T, file: File)

    abstract fun newConfiguration(): T

    abstract fun contains(configuration: T, path: String): Boolean

    abstract fun set(configuration: T, path: String, value: Any?)

    abstract fun setHeader(configuration: T, header: String)

    abstract fun getValues(configuration: T): Map<String, Any?>

    @Throws(ClassCastException::class)
    abstract fun <A> get(configuration: T, path: String): A?

    abstract fun getAny(configuration: T, path: String): Any?
}
