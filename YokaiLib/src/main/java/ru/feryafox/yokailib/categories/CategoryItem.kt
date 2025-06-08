package ru.feryafox.yokailib.categories

import androidx.compose.runtime.Composable

// TODO возможность поддержки нескольких языков
interface CategoryItem {
    val title: String
    val id: String

    @Composable
    fun Content()
}
