package com.artsafin.seriesapp.data

import android.database.AbstractCursor

class ListCursor<T>(private val data: List<T>) : AbstractCursor() {
    private var columns = mutableMapOf<Int, Pair<String, T.() -> Any?>>()

    fun column(index: Int, name: String, valueFn: T.() -> Any?): ListCursor<T> {
        columns.put(index, Pair(name, valueFn))

        return this
    }

    private fun getItem() = data[position]

    override fun getCount() = data.size

    override fun getColumnNames(): Array<out String> {
        val names = columns.values.toMap().keys.toTypedArray()
        return names
    }

    private fun getNullableAny(column: Int) = columns[column]?.second?.invoke(getItem())

    override fun getLong(column: Int) = getNullableAny(column) as Long
    override fun getShort(column: Int) = getNullableAny(column) as Short
    override fun getFloat(column: Int) = getNullableAny(column) as Float
    override fun getDouble(column: Int) = getNullableAny(column) as Double
    override fun getInt(column: Int) = getNullableAny(column) as Int
    override fun getString(column: Int) = getNullableAny(column) as String?

    override fun isNull(column: Int) = getNullableAny(column) == null
}
