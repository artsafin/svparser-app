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
            val playlist = api.episodes(str)
            if (playlist != null) {
//                val flat = normalizePlaylist(playlist, 2)
                return ListCursor(playlist)
                        .column(0, Episodes._ID, { _id })
                        .column(1, Episodes.COMMENT, { comment })
                        .column(2, Episodes.FILE, { file })
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
/*
    private fun normalizePlaylist(pl: Playlist, depth: Int): Playlist {
        val result = Playlist()
        var id: Long = 1

        for (ep in pl) {
            if (!ep.isSingle && depth >= 0) {
                result.addAll(normalizePlaylist(ep.playlist ?: Playlist(), depth - 1))
            } else if (ep.isSingle) {
                result.add(ep.normalize(id++))
            }
        }

        return result
    }*/
}
