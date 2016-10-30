package com.artsafin.seriesapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.text.SimpleDateFormat

import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

import com.artsafin.seriesapp.data.contract.*
import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Playlist
import java.util.*

class Database(context: Context) : SQLiteOpenHelper(context, Database.DB_NAME, null, Database.DB_VERSION) {

    private companion object ConstructorConstants {
        private val DB_VERSION = 4
        private val DB_NAME = "seriesapp.db"
    }

    private val TAG = Database::class.java.simpleName
    private val SERIALS_TABLE = "serials"
    private val SEASONS_TABLE = "seasons"
    private val WATCHES_TABLE = "watches"

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE $SERIALS_TABLE(
                ${Serials._ID} INTEGER PRIMARY KEY,
                ${Serials.NAME} TEXT,
                ${Serials.IMAGE} TEXT,
                ${Serials.DESCRIPTION} TEXT,
                ${Serials.GENRES} TEXT,
                ${Serials.IMG} TEXT,
                ${Serials.ORIGINAL_NAME} TEXT,
                ${Serials.NUM_SEASONS} TEXT,
                ${Serials.FAVORITE} INTEGER DEFAULT 0,
                ${Serials.UPDATE_TS} TEXT)
        """)

        db.execSQL("""
            CREATE TABLE $SEASONS_TABLE(
                ${Seasons._ID} INTEGER PRIMARY KEY,
                ${Seasons.SERIAL_ID} INTEGER,
                ${Seasons.NAME} TEXT,
                ${Seasons.URL} TEXT,
                ${Seasons.YEAR} TEXT,
                ${Seasons.UPDATE_TS} TEXT)
        """)

        db.execSQL("""
            CREATE TABLE $WATCHES_TABLE (
                ${Watches._ID} INTEGER PRIMARY KEY,
                ${Watches.TYPE_ID} INTEGER,
                ${Watches.ITEM_ID} INTEGER,
                ${Watches.SEASON_ID} INTEGER,
                ${Watches.UPDATE_TS} TEXT)
        """)

        Log.d(TAG, "onCreate: Created databases")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade")

        db.execSQL("drop table if exists $SEASONS_TABLE")
        db.execSQL("drop table if exists $SERIALS_TABLE")
        db.execSQL("drop table if exists $WATCHES_TABLE")

        onCreate(db)
    }

    fun findSerialById(serialId: Long): Serial? {
        val c = readableDatabase.query(SERIALS_TABLE,
                                       Serials.ListProjection.FIELDS,
                                       Serials._ID + "=?",
                                       arrayOf(serialId.toString()),
                                       null, null, null, "1")

        if (c.moveToNext()) {
            val s = Serials.ListProjection.toValueObject(c)
            Log.d(TAG, "findSerialById: found record: $s")
            return s
        }

        return null
    }

    fun findSeasonById(seasonId: Long): Season? {
        val c = readableDatabase.query(SEASONS_TABLE,
                                       Seasons.ListProjection.FIELDS,
                                       Seasons._ID + "=?",
                                       arrayOf(seasonId.toString()), null, null, null, "1")

        if (c.moveToNext()) {
            val s = Seasons.ListProjection.toValueObject(c)
            Log.d(TAG, "findSeasonById: found record: $s")
            return s
        }

        return null
    }

    fun serials(search: String?, selArgs: SelectionArgs, loader: (() -> List<Serial>)?): Cursor {
        val c = querySerials(search, selArgs)
        if (loader != null && c.count == 0) {
            Log.d(TAG, "serials: fetch from api, search=$search")
            loadAndInsertSerials(loader)
            return querySerials(search, selArgs)
        } else {
            Log.d(TAG, "serials: found in db, search=$search")
            return c
        }
    }

    private fun querySerials(search: String?, selArgs: SelectionArgs): Cursor {
        var selection = if (search == null || search.length == 0) "1" else Serials.NAME + " like ?"
        var selectionArgs = if (search == null || search.length == 0) arrayOf() else arrayOf("%$search%")

        if (selArgs.selection != null) {
            selection = "($selection) AND (${selArgs.selection})"

            if (selArgs.selectionArgs != null) {
                selectionArgs += selArgs.selectionArgs
            }
        }

        return readableDatabase.query(SERIALS_TABLE, selArgs.projection, selection, selectionArgs, null, null, selArgs.sortOrder)
    }

    private fun loadAndInsertSerials(loader: () -> List<Serial>) {
        val db = writableDatabase
        try {
            db.beginTransaction()

            loader().forEach {
                db.insert(SERIALS_TABLE, null, ContentValues().apply {
                    put(Serials.NAME, it.name)
                    put(Serials.IMAGE, it.image)
                    put(Serials.UPDATE_TS, SimpleDateFormat.getInstance().format(Date()))
                })
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun seasons(serialId: Long, selArgs: SelectionArgs, loader: () -> List<Season>): Cursor {
        val c = querySeasons(serialId, selArgs)
        if (c.count == 0) {
            Log.d(TAG, "seasons: fetching from api id=$serialId")
            loadAndInsertSeasons(serialId, loader)
            return querySeasons(serialId, selArgs)
        } else {
            Log.d(TAG, "seasons: found in db id=$serialId")
            return c
        }
    }

    private fun querySeasons(serialId: Long, selArgs: SelectionArgs) = readableDatabase.query(SEASONS_TABLE,
                                                                                              selArgs.projection,
                                                                                              Seasons.SERIAL_ID + "=?",
                                                                                              arrayOf(serialId.toString()),
                                                                                              null, null, null)

    private fun loadAndInsertSeasons(serialId: Long, loader: () -> List<Season>) {
        val db = writableDatabase
        try {
            db.beginTransaction()

            loader().forEach {
                db.insert(SEASONS_TABLE, null, ContentValues().apply {
                    put(Seasons._ID, it.id)
                    put(Seasons.SERIAL_ID, serialId)
                    put(Seasons.NAME, it.name)
                    put(Seasons.URL, it.url)
                    put(Seasons.YEAR, it.year)
                    put(Seasons.UPDATE_TS, SimpleDateFormat.getInstance().format(Date()))
                })
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateWatched(playlist: Playlist, cb: (Episode, Boolean) -> Unit) {
        val db = readableDatabase

        val binds = Array(playlist.size + 1, { "" })
        binds[0] = Episodes.WATCHED_TYPE_ID.toString()
        System.arraycopy(playlist.map { it._id.toString() }.toTypedArray(), 0, binds, 1, playlist.size)

        val watchedSet: LinkedHashSet<Long>
        var cur: Cursor? = null
        try {
            cur = db.query(WATCHES_TABLE, arrayOf(Watches.ITEM_ID),
                           "${Watches.TYPE_ID}=?"
                                   + " AND ${Watches.ITEM_ID} IN (${Array(playlist.size, { "?" }).joinToString(",")})",
                           binds,
                           null, null, null)

            watchedSet = LinkedHashSet<Long>(cur.count)
            while (cur.moveToNext()) {
                watchedSet.add(cur.getLong(0))
            }
        } finally {
            cur?.close()
        }

        for (ep in playlist) {
            cb(ep, watchedSet.contains(ep._id))
        }
    }

    fun insertWatch(values: ContentValues): Long {
        val db = writableDatabase

        val insertValues = values.apply {
            put(Watches.UPDATE_TS, SimpleDateFormat.getInstance().format(Date()))
        }

        return db.insert(WATCHES_TABLE, null, insertValues)
    }

    fun deleteWatch(selection: String, selectionArgs: Array<String>): Int {
        val db = writableDatabase

        return db.delete(WATCHES_TABLE, selection, selectionArgs)
    }

    fun updateSerials(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        val db = writableDatabase

        Log.d(TAG, "updateSerials: $values, ${selectionArgs.joinToString(",")}")

        return db.update(SERIALS_TABLE, values, selection, selectionArgs)
    }
}
