package com.artsafin.seriesapp.data.contract

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.artsafin.seriesapp.data.ListCursor
import com.artsafin.seriesapp.data.SelectionArgs
import com.artsafin.seriesapp.data.SeriesProvider
import com.artsafin.seriesapp.data.UpdateArgs

import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Playlist
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial
import java.util.*

// non-private for tests
val AUTHORITY = "com.artsafin.seriesapp.data.api.provider.serialapi"

private val baseUri = Uri.parse("content://" + AUTHORITY)

object Serials {
    val PATH = "serials"
    val ALIAS = "serials"

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
            builder.appendQueryParameter(SeriesProvider.PARAM_SEARCH, search)
        }

        return builder.build()
    }

    object Fav {
        fun updateQuery(serial: Serial, favorite: Boolean): Pair<Uri, UpdateArgs> {
            val values = ContentValues().apply {
                put(FAVORITE, if (favorite) 1 else 0)
            }
            return baseUri.buildUpon().appendPath(PATH).build() to UpdateArgs(values, "$_ID = ?", arrayOf(serial.id.toString()))
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
                    cursor.getInt(3) > 0)
        }
    }
}

object Seasons {
    val ALIAS = "seasons"

    val NAME = "name"
    val URL = "url"
    val YEAR = "year"
    val UPDATE_TS = "update_ts"
    val _ID = "_id"
    val SERIAL_ID = "serial_id"

    object BySerial {
        val PATH = "${Serials.PATH}/seasons"
        val MATCHER_ID = 1
        val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

        fun urlSeasonsBySerial(id: Long): Uri {
            return ContentUris.appendId(baseUri.buildUpon().appendEncodedPath(PATH), id).build()
        }
    }

    object Cached {
        val PATH = "seasons"
        val MATCHER_ID = 4
        val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

        fun fetchByIdUrl(id: Long): Pair<Uri, SelectionArgs> {
            val url = baseUri.buildUpon().appendEncodedPath(PATH).build()
            return url to SelectionArgs(
                    Seasons.ListProjection.FIELDS,
                    "$_ID=?", arrayOf(id.toString()))
        }
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, SERIAL_ID, NAME, URL, YEAR)
        val SORT_ORDER = "$NAME ASC"

        fun toValueObject(cursor: Cursor, offset: Int = 0): Season {
            return Season(
                    cursor.getLong(offset + 0),
                    cursor.getLong(offset + 1),
                    cursor.getString(offset + 2),
                    cursor.getString(offset + 3),
                    cursor.getString(offset + 4))
        }
    }
}

object Episodes {
    private val PATH = "episodes"
    val ALIAS = "episodes"
    val MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH

    val _ID = "_id"
    val SEASON_ID = "season_id"
    val FILE = "file"
    val COMMENT = "comment"
    val IS_WATCHED = "watched"
    val UPDATE_TS = "update_ts"

    fun urlOfNewItem(id: Long): Uri {
        return baseUri.buildUpon()
                .appendPath(PATH)
                .appendEncodedPath(id.toString())
                .build()
    }

    object BySeason {
        val PATH = Episodes.PATH
        val MATCHER_ID = 2

        fun fetchBySeasonUrl(id: Long, fetchCached: Boolean = false): Uri {
            val builder = ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id)

            if (fetchCached) {
                builder.appendQueryParameter(SeriesProvider.PARAM_CACHED, "1")
            }

            return builder.build()
        }
    }

    object Cached {
        val PATH = Episodes.PATH
        val MATCHER_ID = 3

        private val url = baseUri.buildUpon().appendPath(PATH).build()

        fun fetchRecentlyWatchedUrl(projection: Array<String>?): Pair<Uri, SelectionArgs> {
            return url to SelectionArgs(projection,
                                        "$ALIAS.$IS_WATCHED=?",
                                        arrayOf("1"),
                                        "$ALIAS.$UPDATE_TS DESC")
        }

        fun insertManyQuery(eps: Set<Episode>): Pair<Uri, Array<ContentValues>> {
            val values = eps.map { it.toContentValues() }
            return Pair(url, values.toTypedArray())
        }

        fun deleteManyQuery(eps: Set<Episode>): Triple<Uri, String?, Array<String>?> {
            val where = "$_ID IN (${Array(eps.size, { "?" }).joinToString(",")})"
            val args = eps.map { it._id.toString() }

            return Triple(url, where, args.toTypedArray())
        }

        fun deleteAllQuery(): Triple<Uri, String?, Array<String>?> {
            val where = ""
            val args = arrayOf<String>()

            return Triple(url, where, args)
        }
    }

    object JoinedSeasonProjection {
        val FIELDS = arrayOf(
                *Episodes.ListProjection.FIELDS.map { "${Episodes.ALIAS}.$it as ${Episodes.ALIAS}_$it" }.toTypedArray(),
                *Seasons.ListProjection.FIELDS.map { "${Seasons.ALIAS}.$it as ${Seasons.ALIAS}_$it" }.toTypedArray()
        )
        val FIELDS_ALIASES = arrayOf(
                *Episodes.ListProjection.FIELDS.map { "${Episodes.ALIAS}_$it" }.toTypedArray(),
                *Seasons.ListProjection.FIELDS.map { "${Seasons.ALIAS}_$it" }.toTypedArray()
        )

        fun toValueObject(cursor: Cursor): Pair<Episode, Season> {
            val e = Episodes.ListProjection.toValueObject(cursor)
            val s = Seasons.ListProjection.toValueObject(cursor, Episodes.ListProjection.FIELDS.size)
            return Pair(e, s)
        }
    }

    object ListProjection {
        val FIELDS = arrayOf(_ID, SEASON_ID, FILE, COMMENT, IS_WATCHED, UPDATE_TS)

        fun toValueObject(cursor: Cursor, offset: Int = 0) = Episode(
            seasonId = cursor.getLong(offset + 1),
            file = cursor.getString(offset + 2),
            comment = cursor.getString(offset + 3),
            isWatched = cursor.getInt(offset + 4) > 0,
            updateTs = cursor.getString(offset + 5) ?: ""
        )

        fun toCursor(episodes: Playlist, offset: Int = 0) = ListCursor(episodes)
                .column(offset + 0, Episodes._ID, { _id })
                .column(offset + 1, Episodes.SEASON_ID, { seasonId })
                .column(offset + 2, Episodes.FILE, { file })
                .column(offset + 3, Episodes.COMMENT, { comment })
                .column(offset + 4, Episodes.IS_WATCHED, { if (isWatched) 1 else 0 })
                .column(offset + 5, Episodes.UPDATE_TS, { updateTs })
    }

    fun Episode.toContentValues() = ContentValues().apply {
        put(_ID, _id)
        put(SEASON_ID, seasonId)
        put(FILE, file)
        put(COMMENT, comment)
        put(IS_WATCHED, if (isWatched) 1 else 0)
    }
}

object ContractMatcher {
    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        if (arrayOf(Serials.MATCHER_ID, Seasons.BySerial.MATCHER_ID, Seasons.Cached.MATCHER_ID,
                    Episodes.BySeason.MATCHER_ID, Episodes.Cached.MATCHER_ID)
                .sum() != 0 + 1 + 2 + 3 + 4) {
            throw RuntimeException("MATCHER_ID not unique")
        }

        addURI(AUTHORITY, Serials.PATH, Serials.MATCHER_ID)
        addURI(AUTHORITY, Seasons.BySerial.PATH + "/#", Seasons.BySerial.MATCHER_ID)
        addURI(AUTHORITY, Seasons.Cached.PATH + "/#", Seasons.Cached.MATCHER_ID)
        addURI(AUTHORITY, Episodes.BySeason.PATH + "/#", Episodes.BySeason.MATCHER_ID)
        addURI(AUTHORITY, Episodes.Cached.PATH, Episodes.Cached.MATCHER_ID)
    }

    fun matchUri(uri: Uri): Int {
        return matcher.match(uri)
    }
}


