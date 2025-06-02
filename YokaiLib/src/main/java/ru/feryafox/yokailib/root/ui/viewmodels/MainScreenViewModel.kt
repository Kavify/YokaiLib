package ru.feryafox.yokailib.root.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.feryafox.yokailib.categories.Category
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    categories: Set<@JvmSuppressWildcards Category>
) : ViewModel() {

    val categories: List<Category> = categories.sortedBy { it.title }

}
