package ru.feryafox.yokailib.settings.base

interface ImmediatelyOnUpdate<T> {
    val immediatelyOnUpdate: (T) -> Unit
    fun immediatelyUpdate(value: T)
}