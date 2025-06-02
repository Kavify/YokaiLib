package ru.feryafox.yokailib.categories

interface Category {
    val title: String
    val items: List<CategoryItem>
    val id: String
}
