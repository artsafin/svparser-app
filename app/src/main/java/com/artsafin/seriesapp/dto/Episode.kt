package com.artsafin.seriesapp.dto

data class Episode(val _id: Long, var comment: String, val file: String, var isWatched: Boolean = false) {
    init {
        comment = comment.replace("<br>", "\n")
    }

    fun updateWatched(flag: Boolean) {
        isWatched = flag
    }
}
