package ru.feryafox.yokailib.storages.base

abstract class BaseStorage(
    val id: String,
    val keys: List<StorageField<*>>
)
