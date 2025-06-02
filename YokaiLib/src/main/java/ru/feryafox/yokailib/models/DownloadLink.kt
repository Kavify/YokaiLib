package ru.feryafox.yokailib.models

data class DownloadLink(
    val title: String,
    val downloadInfo: DownloadInfo
)

abstract class DownloadInfo
