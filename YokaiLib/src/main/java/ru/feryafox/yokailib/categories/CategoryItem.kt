package ru.feryafox.yokailib.categories

import androidx.compose.runtime.Composable

interface CategoryItem {
    val title: String
    val id: String

    @Composable
    fun Content()
}
