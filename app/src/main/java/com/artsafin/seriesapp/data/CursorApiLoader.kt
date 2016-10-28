package com.artsafin.seriesapp.data

import android.database.Cursor

import java.io.IOException

import com.artsafin.seriesapp.data.api.SeriesApi
import com.artsafin.seriesapp.data.contract.Episodes
import com.artsafin.seriesapp.dto.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class CursorApiLoader(private val api: SeriesApi, private val cache: Database) {

    private val TAG = CursorApiLoader::class.java.simpleName

    fun serials(search: String?, selArgs: SelectionArgs): Cursor {
        return cache.serials(search, selArgs, { api.serials(search) ?: listOf() })
    }

    fun seasons(serialId: Long, selArgs: SelectionArgs): Cursor {
        return cache.seasons(serialId, selArgs, fun(): List<Season> {
            val s = cache.findSerialById(serialId)
            return if (s != null) (api.seasons(s.name) ?: listOf()) else listOf()
        })
    }

    fun episodes(seasonId: Long): Cursor? {
        val s = cache.findSeasonById(seasonId) ?: return null

        val client = OkHttpClient()

        val request = Request.Builder().url(s.fullUrl).addHeader("Cookie", "html5default=1;").build()

        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            val str = response.body().string()
            val playlist = api.episodes(s, str)

            if (playlist != null) {
                cache.updateWatched(playlist, Episode::setWatched)
                return Episodes.ListProjection.toCursor(playlist)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (response != null) {
                response.close()
            }
        }

        return null
    }
}
