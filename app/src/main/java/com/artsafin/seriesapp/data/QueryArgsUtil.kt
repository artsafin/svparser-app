package com.artsafin.seriesapp.data

import android.content.ContentValues
import java.util.*

data class UpdateArgs(val values: ContentValues, val where: String, val whereArgs: Array<String>)

data class SelectionArgs(
    val projection: Array<String>? = null,
    val selection: String? = null,
    val selectionArgs: Array<String>? = null,
    val sortOrder: String? = null)
{
    override fun toString(): String {
        val sb = StringBuilder("${javaClass.simpleName}(")

        if (projection?.size ?: 0 > 0) {
            sb.append("projection=${Arrays.toString(projection)}; ")
        }
        if (selection?.length ?: 0 > 0) {
            sb.append("selection=$selection; ")
        }
        if (selectionArgs?.size ?: 0 > 0) {
            sb.append("selectionArgs=${Arrays.toString(selectionArgs)}; ")
        }
        if (sortOrder?.length ?: 0 > 0) {
            sb.append("sortOrder=$sortOrder; ")
        }

        sb.append(")")

        return sb.toString()
    }
}