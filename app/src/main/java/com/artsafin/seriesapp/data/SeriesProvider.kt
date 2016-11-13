package com.artsafin.seriesapp.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.artsafin.seriesapp.data.contract.*

import com.artsafin.seriesapp.data.api.HttpSeriesApi
import com.artsafin.seriesapp.data.db.Database


class SeriesProvider : ContentProvider() {
    companion object {
        val PARAM_SEARCH = "search"
        val PARAM_CACHED = "cached"
    }

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    lateinit private var api: CursorApiLoader
    lateinit private var db: Database

    override fun onCreate(): Boolean {
        db = Database(context)
        api = CursorApiLoader(HttpSeriesApi(), db)

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val selArgs = SelectionArgs(projection, selection, selectionArgs, sortOrder)
        LOGD("query $uri: $selArgs")
        val cached = uri.getBooleanQueryParameter(PARAM_CACHED, false)
        return when (ContractMatcher.matchUri(uri)) {
            Serials.MATCHER_ID -> api.serials(uri.getQueryParameter(PARAM_SEARCH), cached, selArgs)

            Seasons.BySerial.MATCHER_ID -> api.seasonsBySerial(ContentUris.parseId(uri), cached, selArgs)
            Episodes.BySeason.MATCHER_ID -> api.episodesBySeason(ContentUris.parseId(uri), cached)

            Seasons.Cached.MATCHER_ID -> api.seasons(selArgs)
            Episodes.Cached.MATCHER_ID -> api.episodes(selArgs)
            else -> null
        }
    }

    override fun getType(uri: Uri) = when (ContractMatcher.matchUri(uri)) {
        Serials.MATCHER_ID -> Serials.MIME_TYPE_DIR
        Seasons.BySerial.MATCHER_ID -> Seasons.BySerial.MIME_TYPE_DIR
        Seasons.Cached.MATCHER_ID -> Seasons.Cached.MIME_TYPE_DIR
        Episodes.BySeason.MATCHER_ID -> Episodes.MIME_TYPE_DIR
        Episodes.Cached.MATCHER_ID -> Episodes.MIME_TYPE_DIR
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (ContractMatcher.matchUri(uri) == Episodes.Cached.MATCHER_ID) {
            values ?: throw UnsupportedOperationException("Cannot insert null")

            LOGD("insertEpisodes: $values")

            val id = db.episodes.insert(values)

            return if (id >= 0) Episodes.urlOfNewItem(id) else null
        }

        throw UnsupportedOperationException("This url is read only: " + uri.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (ContractMatcher.matchUri(uri) == Episodes.Cached.MATCHER_ID) {
            selection ?: throw UnsupportedOperationException("Arguments must be specified for delete operation: selection")
            selectionArgs ?: throw UnsupportedOperationException("Arguments must be specified for delete operation: selectionArgs")

            LOGD("provider $uri: bulk delete: ${selectionArgs.joinToString(",")}")

            return db.episodes.delete(selection, selectionArgs)
        }

        throw UnsupportedOperationException("This url is read only: " + uri.toString())
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        if (ContractMatcher.matchUri(uri) == Serials.MATCHER_ID) {
            values ?: throw UnsupportedOperationException("Arguments must be specified for update operation: values")
            selection ?: throw UnsupportedOperationException("Arguments must be specified for update operation: selection")
            selectionArgs ?: throw UnsupportedOperationException("Arguments must be specified for update operation: selectionArgs")

            return db.serials.updateSerials(values, selection, selectionArgs)
        }

        throw UnsupportedOperationException("This url is read only: " + uri.toString())
    }
}
