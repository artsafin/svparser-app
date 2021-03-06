package com.artsafin.seriesapp.data.api

import android.net.Uri
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import com.artsafin.seriesapp.data.contract.*
import junit.framework.Assert.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class SeriesProviderContractTest {

    @Test
    fun serialsNullSearch() {
        val uri = Serials.fetchUrl(null)

        assertEquals("content://$AUTHORITY/serials", uri.toString())
    }

    @Test
    fun serialsNonNullSearch() {
        val uri = Serials.fetchUrl("hello world 123")

        assertEquals("content://$AUTHORITY/serials?search=hello%20world%20123", uri.toString())
    }

    @Test
    fun seasonsUrl() {
        val uri = Seasons.BySerial.urlSeasonsBySerial(100500)

        assertEquals("content://$AUTHORITY/seasons/100500", uri.toString())
    }

    @Test
    fun episodesUrl() {
        val uri = Episodes.BySeason.fetchBySeasonUrl(200600)

        assertEquals("content://$AUTHORITY/episodes/200600", uri.toString())
    }
}