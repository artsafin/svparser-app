package com.artsafin.seriesapp.activity

import android.content.Intent
import android.util.Log
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial
import java.io.Serializable

object GlobalViewstate {
    data class DirtyState(private var _dirty: Boolean = false) {
        val isDirty: Boolean
            get() = _dirty

        fun dirty(flag: Boolean = true) {
            _dirty = flag
        }
    }

    val season = DirtyState()
    val serial = DirtyState()
}

class Viewstate(private var _serial: Serial? = null, private var _season: Season? = null): Serializable {
    companion object {
        val EXTRA_VIEWSTATE = "extra_viewstate"
    }

    val serial: Serial?
        get() = _serial

    val season: Season?
        get() = _season

    fun append(serial: Serial): Viewstate {
        this._serial = serial
        return this
    }

    fun append(season: Season): Viewstate {
        this._season = season
        return this
    }
}

fun Intent.with(state: Viewstate): Intent {
    this.putExtra(Viewstate.EXTRA_VIEWSTATE, state)

    return this
}

fun Intent.getViewstate(): Viewstate? {
    if (this.hasExtra(Viewstate.EXTRA_VIEWSTATE)) {
        val state = this.getSerializableExtra(Viewstate.EXTRA_VIEWSTATE) as Viewstate?
        if (state != null) {
            return state
        }
    }
    return null
}
