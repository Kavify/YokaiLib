package ru.feryafox.yokailib.root.storages

import ru.feryafox.yokailib.root.YOKAILIB_ID
import ru.feryafox.yokailib.storages.base.BaseStorage
import ru.feryafox.yokailib.storages.defaultstorages.preferences.StringStorageField
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class YokaiLibStorage @Inject constructor() : BaseStorage(
    id = YOKAILIB_ID,
    keys = listOf(FOO_FIELD)
) {
    companion object {
        val FOO_FIELD = StringStorageField(
            id  = YOKAILIB_ID,
            key = "foo",
            initValue = "Bar"
        )
    }
}
