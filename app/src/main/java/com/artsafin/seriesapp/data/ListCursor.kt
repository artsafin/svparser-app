package com.artsafin.seriesapp.data

import android.database.AbstractCursor
import android.database.MatrixCursor

import java.lang.reflect.Field

class ListCursor<T>(val data: List<T>) : AbstractCursor() {
    private var columns = mutableMapOf<Int, Pair<String, T.() -> Any?>>()

    fun column(index: Int, name: String, valueFn: T.() -> Any?): ListCursor<T> {
        columns.put(index, Pair(name, valueFn))

        return this
    }

    private fun getItem(): T {
        return data[position]
    }

    override fun getCount() = data.size

    override fun getColumnNames(): Array<out String> {
        val names = columns.values.toMap().keys.toTypedArray()
        return names
    }

    override fun getLong(column: Int) = columns[column]?.second?.invoke(getItem()) as Long
    override fun getShort(column: Int) = columns[column]?.second?.invoke(getItem()) as Short
    override fun getFloat(column: Int) = columns[column]?.second?.invoke(getItem()) as Float
    override fun getDouble(column: Int) = columns[column]?.second?.invoke(getItem()) as Double
    override fun getInt(column: Int) = columns[column]?.second?.invoke(getItem()) as Int
    override fun getString(column: Int) = columns[column]?.second?.invoke(getItem()) as String?

    override fun isNull(column: Int) = columns[column]?.second?.invoke(getItem()) == null
}
