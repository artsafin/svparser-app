package com.artsafin.seriesapp.data.api

import android.net.Uri
import android.util.Log
import com.artsafin.seriesapp.dto.Episode

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

import java.io.IOException
import java.util.ArrayList

import com.artsafin.seriesapp.dto.Playlist
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

open class HttpSeriesApi : SeriesApi {
    private val DEFAULT_TRANSLATION_KEY = "default"

    private val TAG = HttpSeriesApi::class.java.simpleName
    private val BASE_URL = "http://138.68.84.5/api"

    private class IdGenerator(val season: Season, startValue: Int = 1) {
        private val MAX_EPISODES = 10000000

        @Volatile private var next: Int = startValue

        @Synchronized fun next() : Long {
            return season.id * MAX_EPISODES + next++
        }
    }

    protected interface RequestExecutor {
        @Throws(IOException::class)
        fun execute(req: Request): Response
    }

    private val gson = Gson()
    protected var executor: RequestExecutor = object : RequestExecutor {
        private val client = OkHttpClient()

        @Throws(IOException::class)
        override fun execute(req: Request): Response {
            return client.newCall(req).execute()
        }
    }

    private fun defaultReq(url: Uri): Request.Builder {
        return Request.Builder().header("Accept", "application/json").url(url.toString())
    }

    override fun serials(search: String?): List<Serial>? {
        try {
            val req = defaultReq(SeriesApi.Contract.serialsUrl(BASE_URL, search)).build()
            val resp = executor.execute(req)
            return gson.fromJson<List<Serial>>(resp.body().string(), object : TypeToken<ArrayList<Serial>>() {}.type)
        } catch (e: IOException) {
            Log.e(TAG, "serials IOException", e)
            e.printStackTrace()
        }

        return null
    }

    override fun seasons(serialName: String): List<Season>? {
        try {
            val req = defaultReq(SeriesApi.Contract.seasonsUrl(BASE_URL, serialName)).build()
            val resp = executor.execute(req)
            val body = resp.body().string()

            Log.d(TAG, "seasons response: " + body)

            return gson.fromJson<List<Season>>(body, object : TypeToken<ArrayList<Season>>() { }.type)
        } catch (e: IOException) {
            Log.e(TAG, "seasons IOException", e)
            e.printStackTrace()
        }

        return null
    }

    override fun episodes(season: Season, seasonHtml: String): Playlist? {
        try {
            val requestBody = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), seasonHtml)
            val req = defaultReq(SeriesApi.Contract.episodesUrl(BASE_URL))
                        .header("Content-Type", "text/html; charset=utf-8")
                        .post(requestBody)
                        .build()
            val resp = executor.execute(req)
            val body = resp.body().string()

            Log.d(TAG, "episodes response: " + body)

            val map = gson.fromJson<JsonObject>(body, JsonObject::class.java)
            if (map.has(DEFAULT_TRANSLATION_KEY)) {
                val flat = hydrateJson(id = IdGenerator(season),
                                       json = map.getAsJsonObject(DEFAULT_TRANSLATION_KEY))

                return flat
            }
        } catch (exc: JsonSyntaxException) {

        } catch (e: IOException) {
            Log.e(TAG, "episodes IOException", e)
            e.printStackTrace()
        }

        return null
    }

    private fun hydrateJson(id: IdGenerator, json: JsonObject, depth: Int = 2): Playlist {
        val result = Playlist()

        if (!json.has("playlist")) {
            return result
        }

        val plArr = json.getAsJsonArray("playlist") ?: return result

        for (elem in plArr) {
            if (elem !is JsonObject) {
                continue
            }
            if (elem.has("playlist") && depth >= 0) {
                val subItems = hydrateJson(id, elem, depth - 1)
                result.addAll(subItems)
            }
            if (elem.has("file")) {
                val file = elem.get("file").asString ?: ""
                result.add(Episode(id.next(), elem.get("comment").asString ?: file, file))
            }
        }

        return result
    }
}
