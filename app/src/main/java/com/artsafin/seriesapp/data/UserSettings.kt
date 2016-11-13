package com.artsafin.seriesapp.data

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

object UserSettings {
    private val PREF_KEY_LAST_UPDATE = "last_update"
    private val LAST_UPDATE_ASK_PERIOD_MS: Long = 7*24*60*60*1000

    private fun LOGD(s: String) = Log.d(javaClass.simpleName, s)

    fun checkUpdateNeeded(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val lastUpdate = pref.getLong(PREF_KEY_LAST_UPDATE, -1)
        LOGD("checkUpdateNeeded: $lastUpdate, ${lastUpdate == -1L}, ${lastUpdate + LAST_UPDATE_ASK_PERIOD_MS}, ${System.currentTimeMillis()}")

        if (lastUpdate == -1L) {
            setLastUpdate(context)
            return false
        } else {
            return lastUpdate + LAST_UPDATE_ASK_PERIOD_MS < System.currentTimeMillis()
        }
    }

    fun setLastUpdate(context: Context, ts: Long = System.currentTimeMillis()) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        LOGD("setLastUpdate: $ts")

        pref.edit()?.run {
            putLong(PREF_KEY_LAST_UPDATE, ts)
            apply()
        }
    }
}