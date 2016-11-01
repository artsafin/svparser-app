package com.artsafin.seriesapp.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private companion object ConstructorConstants {
        private val DB_VERSION = 5
        private val DB_NAME = "seriesapp.db"
    }

    val serials: SerialsRepo by lazy { SerialsRepo(this) }
    val seasons: SeasonsRepo by lazy { SeasonsRepo(this) }
    val episodes: EpisodesRepo by lazy { EpisodesRepo(this) }

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    override fun onCreate(db: SQLiteDatabase) {

        serials.onCreate(db)
        seasons.onCreate(db)
        episodes.onCreate(db)

        LOGD("onCreate: Created databases")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        LOGD("onUpgrade")

        serials.onUpgrade(db, oldVersion, newVersion)
        seasons.onUpgrade(db, oldVersion, newVersion)
        episodes.onUpgrade(db, oldVersion, newVersion)

        onCreate(db)
    }
}
