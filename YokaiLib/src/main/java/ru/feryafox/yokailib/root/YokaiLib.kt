package ru.feryafox.yokailib.root

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.feryafox.yokailib.root.settings.YokaiLibSettings
import ru.feryafox.yokailib.root.storages.YokaiLibStorage
import ru.feryafox.yokailib.settings.base.CategorySettings
import ru.feryafox.yokailib.storages.base.BaseStorage

const val YOKAILIB_ID = "yokailib"

@Module
@InstallIn(SingletonComponent::class)
class YokaiLibModule {

    @Provides
    @IntoSet
    fun provideKavifySettings(impl: YokaiLibSettings): CategorySettings = impl

    @Provides
    @IntoSet
    fun provideKavifyStorage(impl: YokaiLibStorage): BaseStorage = impl
}