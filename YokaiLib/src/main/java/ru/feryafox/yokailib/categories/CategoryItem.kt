package ru.feryafox.yokailib.categories

import androidx.compose.runtime.Composable

interface CategoryItem {
    val title: String
    val id: String
    val content: @Composable () -> Unit
}