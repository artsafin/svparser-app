package com.artsafin.seriesapp.util

import android.widget.ListView

fun ListView.forEach(block: (Int) -> Unit) {
    for (i in 0..count-1) {
        block(i)
    }
}

fun ListView.forEachChecked(block: (Int) -> Unit) {
    val checked = checkedItemPositions ?: return

    for (i in 0..checked.size()-1) {
        if (checked.valueAt(i)) {
            block(checked.keyAt(i))
        }
    }
}

fun ListView.getLastCheckedPosition(): Int? {
    var maxPos = -1
    forEachChecked { maxPos = if (it > maxPos) it else maxPos }

    return if (maxPos >= 0) maxPos else null
}

fun ListView.getFirstCheckedPosition(): Int? {
    val checked = checkedItemPositions ?: return null

    val index = checked.indexOfValue(true)

    return checked.keyAt(index)
}
