package com.artsafin.seriesapp.data.api

import android.database.CursorIndexOutOfBoundsException
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import java.util.Arrays

import com.artsafin.seriesapp.data.ListCursor

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class ListCursorTest {

    private inner class ValueObject(var id: Long, var name: String?)

    private inner class ValueObjectWithPrivate(var id: Long) {
        protected var surname: String? = null
        val name: String

        init {
            this.name = "set in constructor"
        }
    }

    @Test
    fun commonUsecase() {
        val list = Arrays.asList(
                ValueObject(1, "first"),
                ValueObject(2, "second"),
                ValueObject(3, "third"))

        val cursor = ListCursor(list)
                        .column(0, "id", {id})
                        .column(1, "name", {name})

        assertEquals(3, cursor.count.toLong())
        assertEquals(2, cursor.columnCount.toLong())

        assertTrue(cursor.moveToNext())
        assertEquals(1L, cursor.getLong(0))
        assertEquals("first", cursor.getString(1))

        assertTrue(cursor.moveToLast())
        assertEquals(3L, cursor.getLong(0))
        assertEquals("third", cursor.getString(1))
    }

    @Test
    fun valueNull() {
        val list = Arrays.asList(
                ValueObject(0, null))

        val cursor = ListCursor(list)
                .column(0, "id", {id})
                .column(1, "name", {name})

        assertEquals(1, cursor.count.toLong())
        assertEquals(2, cursor.columnCount.toLong())

        assertTrue(cursor.moveToNext())
        assertEquals(0L, cursor.getLong(0))
        assertEquals(null, cursor.getString(1))
    }

    @Test
    fun fieldPrivate() {
        val list = Arrays.asList(
                ValueObjectWithPrivate(0))

        val cursor = ListCursor(list)
                .column(0, "id", {id})

        assertEquals(1, cursor.count.toLong())
        assertEquals(1, cursor.columnCount.toLong())

        assertTrue(cursor.moveToNext())

        assertEquals(0L, cursor.getLong(0))

        assertEquals(null, cursor.getString(1))
    }

    @Test
    fun listEmpty() {
        val list = Arrays.asList<ValueObject>()

        val cursor = ListCursor(list)
                .column(0, "id", {id})
                .column(1, "name", {name})

        assertEquals(0, cursor.count.toLong())
        assertEquals(2, cursor.columnCount.toLong())

        assertFalse(cursor.moveToNext())
    }
}