package com.artsafin.seriesapp.dto

import android.net.Uri

data class Episode(var comment: String, val file: String, val seasonId: Long, var isWatched: Boolean = false, var updateTs: String = "") {
    init {
        comment = comment.replace("<br>", "\n")
    }

    private data class EpisodeId(val comment: String, val file: String, val seasonId: Long)

    val _id: Long
        get() = EpisodeId(comment, Uri.parse(file)?.lastPathSegment ?: "", seasonId).hashCode().toLong()
}
