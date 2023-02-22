package me.nicofisi.minecraft.shulker.config

import kotlin.reflect.KProperty

class COption<A>(
    val configPath: String,
    val defaultValue: A
) {
    var value: A = defaultValue

    fun resetDefaultValue() {
        value = defaultValue
    }

    operator fun provideDelegate(thisRef: CConfig<*>, property: KProperty<*>): COption<A> {
        thisRef.entries.add(this)
        return this
    }

    operator fun getValue(thisRef: CConfig<*>, property: KProperty<*>): A {
        return value
    }

    operator fun setValue(thisRef: CConfig<*>, property: KProperty<*>, value: A) {
        this.value = value
    }

    @Suppress("UNCHECKED_CAST")
    fun deserialize(any: Any) {
        value = any as A
    }

    fun serialize(): Any? = value
}
