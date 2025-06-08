package ru.feryafox.yokailib.categories

// TODO возможность поддержки нескольких языков
interface Category {
    val title: String
    val items: List<CategoryItem>
    val id: String
}
