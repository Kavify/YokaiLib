package ru.feryafox.yokailib.root.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.feryafox.yokailib.settings.base.CategorySettings
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    categories: Set<@JvmSuppressWildcards CategorySettings>
) : ViewModel() {
    val categories: List<CategorySettings> = categories.sortedBy { it.title }
}