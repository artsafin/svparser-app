package com.artsafin.seriesapp.data.api

import android.net.Uri

import com.artsafin.seriesapp.dto.*

interface SeriesApi {
    object Contract {
        fun serialsUrl(baseUrl: String, search: String?): Uri {
            val ub = Uri.parse(baseUrl).buildUpon().appendPath("serial")
            if (search != null) {
                ub.appendQueryParameter("search", search)
            }
            return ub.build()
        }

        fun seasonsUrl(baseUrl: String, serialName: String): Uri {
            return Uri.parse(baseUrl).buildUpon().appendPath("serial").appendPath(serialName).build()
        }

        fun episodesUrl(baseUrl: String): Uri {
            return Uri.parse(baseUrl).buildUpon().appendEncodedPath("episodes/parse").build()
        }
    }

    fun serials(search: String?): List<Serial>?

    fun seasons(serialName: String): List<Season>?

    fun episodes(season: Season, seasonHtml: String): Playlist?
}
