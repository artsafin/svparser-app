package com.artsafin.seriesapp.data.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.artsafin.seriesapp.data.db.Database
import com.artsafin.seriesapp.data.SelectionArgs
import com.artsafin.seriesapp.data.contract.Seasons
import com.artsafin.seriesapp.dto.Season
import java.text.SimpleDateFormat
import java.util.*

class SeasonsRepo(private val db: Database) {
    companion object {
        val TABLE = Seasons.ALIAS
    }

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE(
                ${Seasons._ID} INTEGER PRIMARY KEY,
                ${Seasons.SERIAL_ID} INTEGER,
                ${Seasons.NAME} TEXT,
                ${Seasons.URL} TEXT,
                ${Seasons.YEAR} TEXT,
                ${Seasons.UPDATE_TS} TEXT)
        """)
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE")
    }


    fun findById(seasonId: Long): Season? {
        val c = db.readableDatabase.query(TABLE,
                                       Seasons.ListProjection.FIELDS,
                                       Seasons._ID + "=?",
                                       arrayOf(seasonId.toString()), null, null, null, "1")

        if (c.moveToNext()) {
            val s = Seasons.ListProjection.toValueObject(c)
            LOGD("findById: found record: $s")
            return s
        }

        return null
    }

    fun fetchOrLoadBySerial(serialId: Long, useCache: Boolean, selArgs: SelectionArgs, loader: () -> List<Season>): Cursor {
        val c = fetchBySerial(serialId, selArgs)
        if (!useCache || c.count == 0) {
            LOGD("seasons: fetching from api id=$serialId")
            loadAndInsertSeasons(loader)
            return fetchBySerial(serialId, selArgs)
        } else {
            LOGD("seasons: found in db id=$serialId")
            return c
        }
    }

    private fun fetchBySerial(serialId: Long, selArgs: SelectionArgs)
            = fetch(selArgs.copy(selection = "${Seasons.SERIAL_ID}=?", selectionArgs = arrayOf(serialId.toString())))

    fun fetch(selArgs: SelectionArgs): Cursor
            = db.readableDatabase.query(TABLE, selArgs.projection, selArgs.selection, selArgs.selectionArgs,
                                        null, null, selArgs.sortOrder)

    private fun loadAndInsertSeasons(loader: () -> List<Season>) {
        val db = db.writableDatabase
        try {
            db.beginTransaction()

            db.delete(TABLE, "", arrayOf())

            loader().forEach {
                db.insert(TABLE, null, ContentValues().apply {
                    put(Seasons._ID, it.id)
                    put(Seasons.SERIAL_ID, it.serialId)
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
}