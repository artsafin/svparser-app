package com.artsafin.seriesapp.data

import android.database.Cursor
import android.support.v4.util.LruCache
import android.util.Log

import java.io.IOException

import com.artsafin.seriesapp.data.api.SeriesApi
import com.artsafin.seriesapp.data.contract.Episodes
import com.artsafin.seriesapp.data.db.Database
import com.artsafin.seriesapp.dto.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class CursorApiLoader(private val api: SeriesApi, private val cache: Database) {

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)
    private val playlistCache = LruCache<Long, Playlist>(1)

    fun serials(search: String?, selArgs: SelectionArgs): Cursor {
        if (search == null && (selArgs.selection?.length ?: 0) > 0) {
            LOGD("serials: fetching from cache")
            return cache.serials.fetch(null, selArgs)
        } else {
            return cache.serials.fetchOrLoad(search, selArgs, { api.serials(search) ?: listOf() })
        }
    }

    fun seasonsBySerial(serialId: Long, selArgs: SelectionArgs): Cursor {
        return cache.seasons.fetchOrLoadBySerial(serialId, selArgs, fun(): List<Season> {
            val s = cache.serials.findById(serialId) ?: return listOf()
            val apiResult = api.seasons(s.name) ?: return listOf()
            return apiResult.map { it.serialId = serialId; it }
        })
    }

    fun seasons(selArgs: SelectionArgs) = cache.seasons.fetch(selArgs)

    private fun loadPlaylist(seasonId: Long): Playlist? {
        val s = cache.seasons.findById(seasonId) ?: return null

        val client = OkHttpClient()

        val request = Request.Builder().url(s.fullUrl).addHeader("Cookie", "html5default=1;").build()

        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            val str = response.body().string()
            val playlist = api.episodes(s, str)

            return playlist
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (response != null) {
                response.close()
            }
        }
        return null
    }

    fun episodesBySeason(seasonId: Long, cached: Boolean): Cursor? {
        val playlist = if (cached) {
            LOGD("episodesBySeason: fetching cached")
            playlistCache.get(seasonId) ?: loadPlaylist(seasonId) ?: return null
        } else {
            LOGD("episodesBySeason: fetching from db")
            loadPlaylist(seasonId) ?: return null
        }

        playlistCache.put(seasonId, playlist)

        cache.episodes.joinInplace(playlist)
        return Episodes.ListProjection.toCursor(playlist)
    }

    fun episodes(selArgs: SelectionArgs) = cache.episodes.fetch(selArgs)
}
