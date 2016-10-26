package com.artsafin.seriesapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.text.SimpleDateFormat
import java.util.Date

import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial

import com.artsafin.seriesapp.data.contract.*

class Database(context: Context) : SQLiteOpenHelper(context, Database.DB_NAME, null, Database.DB_VERSION) {

    private companion object ConstructorConstants {
        private val DB_VERSION = 2
        private val DB_NAME = "seriesapp.db"
    }

    interface Loader<T> {
        fun load(): List<T>
    }

    private val TAG = Database::class.java.simpleName
    private val SERIALS_TABLE = "serials"
    private val SEASONS_TABLE = "seasons"

    override fun onCreate(db: SQLiteDatabase) {

        /*
        {"id":14242,"commonName":"Теория большого взрыва",
        "name":"Сериал Теория большого взрыва/The Big Bang Theory 10 сезон",
        "url":"/serial-14242-Teoriya_bol_shogo_vzryva-10-season.html",
        "description":"Наука и только наука! Вот ...",
        "genres":["комедия"],
        "year":"2016",
        "img":"http://cdn.seasonvar.ru/oblojka/14242.jpg",
        "originalName":"The Big Bang Theory",
        "numSeasons":10}
         */

        db.execSQL("CREATE TABLE " + SERIALS_TABLE + "(" +
                           Serials._ID + " INTEGER PRIMARY KEY, " +
                           Serials.NAME + " TEXT, " +
                           Serials.IMAGE + " TEXT, " +
                           Serials.DESCRIPTION + " TEXT, " +
                           Serials.GENRES + " TEXT, " +
                           Serials.IMG + " TEXT, " +
                           Serials.ORIGINAL_NAME + " TEXT, " +
                           Serials.NUM_SEASONS + " TEXT, " +
                           Serials.UPDATE_TS + " TEXT" +
                           ")")

        db.execSQL("CREATE TABLE " + SEASONS_TABLE + "(" +
                           Seasons._ID + " INTEGER PRIMARY KEY, " +
                           Seasons.SERIAL_ID + " INTEGER, " +
                           Seasons.NAME + " TEXT, " +
                           Seasons.URL + " TEXT, " +
                           Seasons.YEAR + " TEXT, " +
                           Seasons.UPDATE_TS + " TEXT" +
                           ")")

        Log.d(TAG, "onCreate: Created databases")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade")

        db.execSQL("drop table " + SEASONS_TABLE)
        db.execSQL("drop table " + SERIALS_TABLE)

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
            Log.d(TAG, "findSerialById: found record: " + s.toString())
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
            Log.d(TAG, "findSeasonById: found record: " + s.toString())
            return s
        }

        return null
    }

    fun serials(search: String?, selArgs: SelectionArgs, loader: () -> List<Serial>): Cursor {
        val c = querySerials(search, selArgs)
        if (c.count == 0) {
            Log.d(TAG, "serials: fetch from api, search=" + search)
            loadAndInsertSerials(loader)
            return querySerials(search, selArgs)
        } else {
            Log.d(TAG, "serials: found in db, search=" + search)
            return c
        }
    }

    private fun querySerials(search: String?, selArgs: SelectionArgs): Cursor {
        val selection = if (search == null || search.length == 0) null else Serials.NAME + " like ?"
        val selectionArgs = if (search == null || search.length == 0) null else arrayOf("%$search%")

        return readableDatabase.query(SERIALS_TABLE, selArgs.projection, selection, selectionArgs, null, null, selArgs.sortOrder)
    }

    private fun loadAndInsertSerials(loader: () -> List<Serial>) {
        val db = writableDatabase
        try {
            db.beginTransaction()

            loader().forEach {
                db.insert(SERIALS_TABLE, null, with (ContentValues()) {
                    put(Serials.NAME, it.name)
                    put(Serials.IMAGE, it.image)
                    put(Serials.UPDATE_TS, SimpleDateFormat.getInstance().format(Date()))
                    this
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
            Log.d(TAG, "seasons: fetching from api id=" + serialId.toString())
            loadAndInsertSeasons(serialId, loader)
            return querySeasons(serialId, selArgs)
        } else {
            Log.d(TAG, "seasons: found in db id=" + serialId.toString())
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
                db.insert(SEASONS_TABLE, null, with(ContentValues()) {
                    put(Seasons._ID, it.id)
                    put(Seasons.SERIAL_ID, serialId)
                    put(Seasons.NAME, it.name)
                    put(Seasons.URL, it.url)
                    put(Seasons.YEAR, it.year)
                    put(Seasons.UPDATE_TS, SimpleDateFormat.getInstance().format(Date()))
                    this
                })
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
