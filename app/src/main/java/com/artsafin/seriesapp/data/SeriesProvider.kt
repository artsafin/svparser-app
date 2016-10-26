package com.artsafin.seriesapp.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.artsafin.seriesapp.data.contract.*

import com.artsafin.seriesapp.data.api.HttpSeriesApi


class SeriesProvider : ContentProvider() {
    private var api: CursorApiLoader? = null

    override fun onCreate(): Boolean {
        api = CursorApiLoader(HttpSeriesApi(), Database(context))

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val selArgs = SelectionArgs(projection, selection, selectionArgs, sortOrder)
        return when (ContractMatcher.matchUri(uri)) {
            Serials.MATCHER_ID -> api?.serials(uri.getQueryParameter("search"), selArgs)
            Seasons.MATCHER_ID -> api?.seasons(ContentUris.parseId(uri), selArgs)
            Episodes.MATCHER_ID -> api?.episodes(ContentUris.parseId(uri))
            else -> null
        }
    }

    override fun getType(uri: Uri) = when (ContractMatcher.matchUri(uri)) {
        Serials.MATCHER_ID -> Serials.MIME_TYPE_DIR
        Seasons.MATCHER_ID -> Seasons.MIME_TYPE_DIR
        Episodes.MATCHER_ID -> Episodes.MIME_TYPE_DIR
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Serial API provider is read only")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Serial API provider is read only")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Serial API provider is read only")
    }
}
