package ru.feryafox.yokailib.categories

import androidx.compose.runtime.Composable

// TODO возможность поддержки нескольких языков
interface CategoryItem {
    val title: String
    val id: String
    val onSelected: () -> Unit
        get() = {}

    @Composable
    fun Content()
}
