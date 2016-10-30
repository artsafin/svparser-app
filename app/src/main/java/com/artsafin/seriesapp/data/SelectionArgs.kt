package com.artsafin.seriesapp.data

import java.util.*

class SelectionArgs(
    val projection: Array<String>?,
    val selection: String?,
    val selectionArgs: Array<String>?,
    val sortOrder: String?)
{
    override fun toString(): String {
        return "SelectionArgs(projection=${Arrays.toString(projection)}, selection=$selection, selectionArgs=${Arrays.toString(
                selectionArgs)}, sortOrder=$sortOrder)"
    }
}