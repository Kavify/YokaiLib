package ru.feryafox.yokailib.root.storages

import ru.feryafox.yokailib.root.YOKAILIB_ID
import ru.feryafox.yokailib.storages.base.BaseStorage
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class YokaiLibStorage @Inject constructor() : BaseStorage(
    id = YOKAILIB_ID,
    keys = listOf()
)
