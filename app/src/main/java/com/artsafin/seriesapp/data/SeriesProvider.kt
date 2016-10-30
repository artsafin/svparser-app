package com.artsafin.seriesapp.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.artsafin.seriesapp.data.contract.*

import com.artsafin.seriesapp.data.api.HttpSeriesApi


class SeriesProvider : ContentProvider() {
    private val TAG = SeriesProvider::class.java.simpleName

    lateinit private var api: CursorApiLoader
    lateinit private var db: Database

    override fun onCreate(): Boolean {
        db = Database(context)
        api = CursorApiLoader(HttpSeriesApi(), db)

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val selArgs = SelectionArgs(projection, selection, selectionArgs, sortOrder)
        return when (ContractMatcher.matchUri(uri)) {
            Serials.MATCHER_ID -> api.serials(uri.getQueryParameter("search"), selArgs)
            Seasons.MATCHER_ID -> api.seasons(ContentUris.parseId(uri), selArgs)
            Episodes.MATCHER_ID -> api.episodes(ContentUris.parseId(uri), uri.getBooleanQueryParameter("cached", false))
            else -> null
        }
    }

    override fun getType(uri: Uri) = when (ContractMatcher.matchUri(uri)) {
        Serials.MATCHER_ID -> Serials.MIME_TYPE_DIR
        Seasons.MATCHER_ID -> Seasons.MIME_TYPE_DIR
        Episodes.MATCHER_ID -> Episodes.MIME_TYPE_DIR
        Watches.MATCHER_ID -> Watches.MIME_TYPE
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (ContractMatcher.matchUri(uri) != Watches.MATCHER_ID) {
            throw UnsupportedOperationException("This url is read only: " + uri.toString())
        }
        if (values == null) {
            throw UnsupportedOperationException("Cannot insert null")
        }

        val typeId = values.getAsLong(Watches.TYPE_ID) ?: throw UnsupportedOperationException("Cannot insert null: typeId")
        val itemId = values.getAsLong(Watches.ITEM_ID) ?: throw UnsupportedOperationException("Cannot insert null: itemId")

        Log.d(TAG, "provider $uri: insert: $itemId")

        val id = db.insertWatch(typeId, itemId)

        return Watches.newUrl(id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (ContractMatcher.matchUri(uri) != Watches.MATCHER_ID) {
            throw UnsupportedOperationException("This url is read only: " + uri.toString())
        }

        selection ?: throw UnsupportedOperationException("Arguments must be specified for delete operation: selection")
        selectionArgs ?: throw UnsupportedOperationException("Arguments must be specified for delete operation: selectionArgs")

        Log.d(TAG, "provider $uri: bulk delete: ${selectionArgs.joinToString(",")}")

        return db.bulkRemoveWatch(selection, selectionArgs)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Serial API provider is read only")
    }
}
