package com.artsafin.seriesapp.activity

import android.content.Intent
import android.util.Log
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial
import java.io.Serializable

object GlobalViewstate {
    val season = DirtyState()
    val serial = DirtyState()
    val episode = DirtyState()

    val viewState = ViewState()
}

data class DirtyState(private var _dirty: Boolean = false) {
    private val skippedLoaders = mutableSetOf<Int>()

    fun dirty(flag: Boolean = true) {
        _dirty = flag
        if (flag) {
            skippedLoaders.clear()
        }
    }

    fun ifDirty(loaderId: Int, fn: () -> Unit) {
        if (_dirty && !skippedLoaders.contains(loaderId)) {
            fn()
            skippedLoaders.add(loaderId)
        }
    }
}

class ViewState(private var _serial: Serial? = null, private var _season: Season? = null): Serializable {
    companion object {
        val EXTRA_VIEWSTATE = "extra_viewstate"
    }

    val serial: Serial?
        get() = _serial

    val season: Season?
        get() = _season

    fun append(serial: Serial): ViewState {
        this._serial = serial
        return this
    }

    fun append(season: Season): ViewState {
        this._season = season
        return this
    }
}

fun Intent.with(state: ViewState): Intent {
    this.putExtra(ViewState.EXTRA_VIEWSTATE, state)

    return this
}

fun Intent.getViewstate(): ViewState? {
    if (this.hasExtra(ViewState.EXTRA_VIEWSTATE)) {
        val state = this.getSerializableExtra(ViewState.EXTRA_VIEWSTATE) as ViewState?
        if (state != null) {
            return state
        }
    }
    return null
}
