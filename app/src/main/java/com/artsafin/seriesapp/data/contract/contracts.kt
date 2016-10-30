package com.artsafin.seriesapp.data.contract

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.artsafin.seriesapp.data.ListCursor

import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Playlist
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial
import java.util.*

data class UpdateArgs(val url: Uri, val values: ContentValues, val where: String, val whereArgs: Array<String>)

// non-private for tests
val AUTHORITY = "com.artsafin.seriesapp.data.api.provider.serialapi"

private val baseUri = Uri.parse("content://" + AUTHORITY)

object Serials {
    val PATH = "serials"

    val MATCHER_ID = 0
    val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

    val FAVORITE = "favorite"
    val NAME = "name"
    val IMAGE = "image"
    val _ID = "_id"

    val DESCRIPTION = "description"
    val GENRES = "genres"
    val IMG = "img"
    val ORIGINAL_NAME = "original_name"
    val NUM_SEASONS = "num_seasons"
    val UPDATE_TS = "update_ts"

    fun fetchUrl(search: String?): Uri {
        val builder = baseUri.buildUpon().appendPath(PATH)

        if (search != null) {
            builder.appendQueryParameter("search", search)
        }

        return builder.build()
    }

    object Fav {
        fun updateQuery(serial: Serial, favorite: Boolean): UpdateArgs {
            val values = ContentValues().apply {
                put(FAVORITE, if (favorite) 1 else 0)
            }
            return UpdateArgs(baseUri.buildUpon().appendPath(PATH).build(), values, "$_ID = ?", arrayOf(serial.id.toString()))
        }

        val where: String? = "$FAVORITE = ?"
        fun whereArgs(favorite: Boolean): Array<String> = if (favorite) arrayOf("1") else arrayOf("0")
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, NAME, IMAGE, FAVORITE)
        val SORT_ORDER = NAME + " ASC"

        fun toValueObject(cursor: Cursor): Serial {
            return Serial(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Serial.Flags(favorite = cursor.getInt(3) > 0))
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
    val IS_WATCHED = "watched"

    val WATCHED_TYPE_ID = 1

    fun urlEpisodesBySeason(id: Long, fetchCached: Boolean = false): Uri {
        val builder = ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id)

        if (fetchCached) {
            builder.appendQueryParameter("cached", "1")
        }

        return builder.build()
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, COMMENT, FILE, IS_WATCHED)

        fun toValueObject(cursor: Cursor): Episode {
            return Episode(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3) > 0)
        }

        fun toCursor(episodes: Playlist): Cursor {
            return ListCursor(episodes)
                    .column(0, Episodes._ID, { _id })
                    .column(1, Episodes.COMMENT, { comment })
                    .column(2, Episodes.FILE, { file })
                    .column(3, Episodes.IS_WATCHED, { if (isWatched) 1 else 0 })
        }
    }
}

object Watches {
    val PATH = "watches"
    val MATCHER_ID = 3
    val MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH

    val _ID = "_id"
    val TYPE_ID = "type_id"
    val ITEM_ID = "item_id"
    val SEASON_ID = "season_id"
    val UPDATE_TS = "update_ts"

    fun newUrl(watchId: Long): Uri {
        return baseUri.buildUpon()
                .appendPath(PATH)
                .appendEncodedPath(watchId.toString())
                .build()
    }

    fun insertManyQuery(seasonId: Long, eps: Set<Long>): Pair<Uri, Array<ContentValues>> {
        val values = eps.map {
            ContentValues().apply {
                put(TYPE_ID, Episodes.WATCHED_TYPE_ID)
                put(ITEM_ID, it)
                put(SEASON_ID, seasonId)
            }
        }
        return Pair(baseUri.buildUpon().appendPath(PATH).build(), values.toTypedArray())
    }

    fun deleteManyQuery(eps: Set<Long>): Triple<Uri, String?, Array<String>?> {
        val where = "$TYPE_ID = ? AND $ITEM_ID IN (${Array(eps.size, { "?" }).joinToString(",")})"
        val args = arrayListOf(Episodes.WATCHED_TYPE_ID.toString())
        args.addAll(eps.map(Long::toString))

        return Triple(baseUri.buildUpon().appendPath(PATH).build(), where, args.toTypedArray())
    }
}

object ContractMatcher {
    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, Serials.PATH, Serials.MATCHER_ID)
        addURI(AUTHORITY, Seasons.PATH + "/#", Seasons.MATCHER_ID)
        addURI(AUTHORITY, Episodes.PATH + "/#", Episodes.MATCHER_ID)
        addURI(AUTHORITY, Watches.PATH, Watches.MATCHER_ID)
    }

    fun matchUri(uri: Uri): Int {
        return matcher.match(uri)
    }
}


