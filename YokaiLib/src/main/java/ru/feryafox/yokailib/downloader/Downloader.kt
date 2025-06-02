package ru.feryafox.yokailib.downloader

import android.content.Context
import ru.feryafox.yokailib.models.DownloadLink

interface Downloader {
    suspend fun downloadBook(
        context: Context,
        downloadLink: DownloadLink,
        onProgressUpdate: (Int) -> Unit
    )
}