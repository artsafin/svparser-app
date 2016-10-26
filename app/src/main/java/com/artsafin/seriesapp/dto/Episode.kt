package com.artsafin.seriesapp.dto

data class Episode(val _id: Long, var comment: String, val file: String) {
    var playlist: Playlist? = null

    init {
        comment = comment.replace("<br>", "\n")
    }

    val isSingle: Boolean
        get() = playlist == null
}
