package com.artsafin.seriesapp.data.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.artsafin.seriesapp.data.SelectionArgs
import com.artsafin.seriesapp.data.contract.Episodes
import com.artsafin.seriesapp.data.contract.Seasons
import com.artsafin.seriesapp.dto.Playlist
import java.text.SimpleDateFormat
import java.util.*

class EpisodesRepo(private val db: Database) {
    companion object {
        private val TABLE = Episodes.ALIAS
    }
    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)


    fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE (
                ${Episodes._ID} INTEGER PRIMARY KEY,
                ${Episodes.SEASON_ID} INTEGER,
                ${Episodes.FILE} TEXT,
                ${Episodes.COMMENT} TEXT,
                ${Episodes.IS_WATCHED} INTEGER,
                ${Episodes.UPDATE_TS} TEXT)
        """)
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO: make it more user friendly
        db.execSQL("drop table if exists $TABLE")
    }


    fun joinInplace(playlist: Playlist) {
        val db = db.readableDatabase

        val watchedSet = LinkedHashMap<Long, Pair<Boolean, String>>(playlist.size)
        var cur: Cursor? = null
        try {
            cur = db.query(TABLE,
                           arrayOf(Episodes._ID, Episodes.IS_WATCHED, Episodes.UPDATE_TS),
                           "${Episodes._ID} IN (${Array(playlist.size, { "?" }).joinToString(",")})",
                           playlist.map { it._id.toString() }.toTypedArray(),
                           null, null, null)

            while (cur.moveToNext()) {
                watchedSet.put(cur.getLong(0), Pair(cur.getInt(1) > 0, cur.getString(2)))
            }
        } finally {
            cur?.close()
        }

        for (ep in playlist) {
            val (isWatched, updateTs) = watchedSet[ep._id] ?: Pair(false, "")
            ep.isWatched = isWatched
            ep.updateTs = updateTs
        }
    }

    fun insert(values: ContentValues): Long {
        val db = db.writableDatabase

        val insertValues = values.apply {
            put(Episodes.UPDATE_TS, SimpleDateFormat.getInstance().format(Date()))
        }

        return db.insertWithOnConflict(TABLE, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        val db = db.writableDatabase

        return db.delete(TABLE, selection, selectionArgs)
    }

    fun fetch(selArgs: SelectionArgs): Cursor? {
        val db = db.readableDatabase

        if (selArgs.projection?.size ?: 0 > Episodes.ListProjection.FIELDS.size) {
            return fetchJoinedAll(selArgs)
        }

        return db.query(TABLE, selArgs.projection, selArgs.selection, selArgs.selectionArgs, null, null, selArgs.sortOrder, null)
    }

    fun fetchJoinedAll(selArgs: SelectionArgs): Cursor? {
        selArgs.projection ?: return null

        val projectionWithId = arrayOf(*selArgs.projection,
                                       "${SeasonsRepo.TABLE}.${Seasons._ID}*10000000+${EpisodesRepo.TABLE}.${Episodes._ID} as _id")

        val builder = SQLiteQueryBuilder()
        builder.tables = "${EpisodesRepo.TABLE} inner join ${SeasonsRepo.TABLE} on ${SeasonsRepo.TABLE}.${Seasons._ID}=${EpisodesRepo.TABLE}.${Episodes.SEASON_ID}"
        val sql = builder.buildQuery(projectionWithId, selArgs.selection, null, null, selArgs.sortOrder, null)

        val db = db.readableDatabase
        return db.rawQuery(sql, selArgs.selectionArgs)
    }
}