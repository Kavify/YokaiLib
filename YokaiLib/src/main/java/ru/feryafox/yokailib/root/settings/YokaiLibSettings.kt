package ru.feryafox.yokailib.root.settings

import ru.feryafox.yokailib.root.YOKAILIB_ID
import ru.feryafox.yokailib.settings.base.CategorySettings
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class YokaiLibSettings @Inject constructor() : CategorySettings(
    YOKAILIB_ID,
    "YokaiLib",
    listOf()
)
