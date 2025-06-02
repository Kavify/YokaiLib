package ru.feryafox.yokailib.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.feryafox.yokailib.models.BookDetail
import ru.feryafox.yokailib.models.CurrentBookDetail

abstract class BaseSearchScreenViewModel<
        B: BookDetail,
        C: CurrentBookDetail
> {
    val books = mutableStateListOf<B>()
    val search by mutableStateOf("")
    var isLoading by mutableStateOf(false)
        private set
    var currentBook by mutableStateOf<C?>(null)
        private set


    abstract fun searchBook(query: String): List<C>

    abstract fun selectCurrent(selectedBook: B): C
}