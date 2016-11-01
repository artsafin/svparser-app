package com.artsafin.seriesapp.data.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.artsafin.seriesapp.data.db.Database
import com.artsafin.seriesapp.data.SelectionArgs
import com.artsafin.seriesapp.data.contract.Serials
import com.artsafin.seriesapp.dto.Serial
import java.text.SimpleDateFormat
import java.util.*

class SerialsRepo(private val db: Database) {
    val TABLE = Serials.ALIAS

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE(
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
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE")
    }

    fun findById(serialId: Long): Serial? {
        val c = db.readableDatabase.query(TABLE,
                                       Serials.ListProjection.FIELDS,
                                       Serials._ID + "=?",
                                       arrayOf(serialId.toString()),
                                       null, null, null, "1")

        if (c.moveToNext()) {
            val s = Serials.ListProjection.toValueObject(c)
            LOGD("findById: found record: $s")
            return s
        }

        return null
    }

    fun fetchOrLoad(search: String?, selArgs: SelectionArgs, loader: () -> List<Serial>): Cursor {
        val c = fetch(search, selArgs)
        if (c.count == 0) {
            LOGD("serials: fetch from api, search=$search")
            loadAndInsertSerials(loader)
            return fetch(search, selArgs)
        } else {
            LOGD("serials: found in db, search=$search")
            return c
        }
    }

    fun fetch(search: String?, selArgs: SelectionArgs): Cursor {
        var selection = if (search == null || search.length == 0) "1" else Serials.NAME + " like ?"
        var selectionArgs = if (search == null || search.length == 0) arrayOf() else arrayOf("%$search%")

        if (selArgs.selection != null) {
            selection = "($selection) AND (${selArgs.selection})"

            if (selArgs.selectionArgs != null) {
                selectionArgs += selArgs.selectionArgs
            }
        }

        return db.readableDatabase.query(TABLE, selArgs.projection, selection, selectionArgs, null, null, selArgs.sortOrder)
    }

    private fun loadAndInsertSerials(loader: () -> List<Serial>) {
        val db = db.writableDatabase
        try {
            db.beginTransaction()

            loader().forEach {
                db.insert(TABLE, null, ContentValues().apply {
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

    fun updateSerials(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        val db = db.writableDatabase

        LOGD("updateSerials: $values, ${selectionArgs.joinToString(",")}")

        return db.update(TABLE, values, selection, selectionArgs)
    }
}