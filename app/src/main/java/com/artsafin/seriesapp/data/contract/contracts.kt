package com.artsafin.seriesapp.data.contract

import android.content.ContentResolver
import android.content.ContentUris
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

// non-private for tests
val AUTHORITY = "com.artsafin.seriesapp.data.api.provider.serialapi"

private val baseUri = Uri.parse("content://" + AUTHORITY)

object Serials {
    val PATH = "serials"
    val MATCHER_ID = 0
    val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

    val NAME = "name"
    val IMAGE = "image"
    val _ID = "_id"
    val DESCRIPTION = "description"
    val GENRES = "genres"
    val IMG = "img"
    val ORIGINAL_NAME = "original_name"
    val NUM_SEASONS = "num_seasons"
    val UPDATE_TS = "update_ts"

    fun urlSerials(search: String?): Uri {
        val builder = baseUri.buildUpon().appendPath(PATH)

        if (search != null) {
            builder.appendQueryParameter("search", search)
        }

        return builder.build()
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, NAME, IMAGE)
        val SORT_ORDER = NAME + " ASC"

        fun toValueObject(cursor: Cursor): Serial {
            return Serial(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2))
        }
    }
}

object Seasons {
    val PATH = "seasons"
    val MATCHER_ID = 1
    val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

    val NAME = "name"
    val URL = "url"
    val YEAR = "year"
    val UPDATE_TS = "update_ts"
    val _ID = "_id"
    val SERIAL_ID = "serial_id"

    fun urlSeasonsBySerial(id: Long): Uri {
        return ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id).build()
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, SERIAL_ID, NAME, URL, YEAR)
        val SORT_ORDER = NAME + " ASC"

        fun toValueObject(cursor: Cursor): Season {
            return Season(
                    cursor.getLong(0),
                    cursor.getLong(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4))
        }
    }
}

object Episodes {
    val PATH = "episodes"
    val MATCHER_ID = 2
    val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

    val _ID = "_id"
    val COMMENT = "comment"
    val FILE = "file"

    fun urlEpisodesBySeason(id: Long): Uri {
        return ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id).build()
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, COMMENT, FILE)

        fun toValueObject(cursor: Cursor): Episode {
            return Episode(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2))
        }
    }
}

object ContractMatcher {
    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        matcher.addURI(AUTHORITY, Serials.PATH, Serials.MATCHER_ID)
        matcher.addURI(AUTHORITY, Seasons.PATH + "/#", Seasons.MATCHER_ID)
        matcher.addURI(AUTHORITY, Episodes.PATH + "/#", Episodes.MATCHER_ID)
    }

    fun matchUri(uri: Uri): Int {
        return matcher.match(uri)
    }
}


