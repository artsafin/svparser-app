package com.artsafin.seriesapp.data.api

import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Arrays

import com.artsafin.seriesapp.dto.Episode
import com.artsafin.seriesapp.dto.Playlist
import com.artsafin.seriesapp.dto.Season
import com.artsafin.seriesapp.dto.Serial
import okhttp3.Response

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class HttpSeriesApiTest {

    @Before
    fun setUp() {

    }

    @Test
    @Throws(Exception::class)
    fun serials() {
        val api = TestHttpSeriesApi(Response.Builder(),
                                    TestData.serials)

        val list = api.serials(null)

        val expected = Arrays.asList(
                Serial(0, "Скалолазка", "http://cdn.seasonvar.ru/oblojka/12860.jpg"),
                Serial(0, "Лалола", "http://cdn.seasonvar.ru/oblojka/1635.jpg"))

        assertEquals(expected, list)
    }

    @Test
    @Throws(Exception::class)
    fun seasons() {
        val api = TestHttpSeriesApi(Response.Builder(),
                                    TestData.seasons)

        val list = api.seasons("Whatever")

        val expected = Arrays.asList(
                Season(12909, 0, "Сериал Шерлок/Sherlock 4 сезон", "/serial-12909-SHerlok-4-sezon.html", "2016"),
                Season(8328, 0, "Сериал Шерлок/Sherlock 3 сезон", "/serial-8328-SHerlok-3-sezon.html", "2016"))

        assertEquals(expected, list)

    }

    @Test
    @Throws(Exception::class)
    fun episodes() {
        val api = TestHttpSeriesApi(Response.Builder(),
                                    TestData.episodesOne)

        val list = api.episodes("Whatever")

        val expected = Arrays.asList(
                Episode(1, "0 серия SD/HD<br>BaibaKo",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e00.White.Christmas.HDTV720p.Rus.Eng.BaibaKo.tv.a1.25.11.15.mp4"))

        assertEquals(expected, list)

    }

    @Test
    @Throws(Exception::class)
    fun episodesMany() {
        val api = TestHttpSeriesApi(Response.Builder(),
                                    TestData.episodesMany)

        val list = api.episodes("Whatever")

        val expected = Arrays.asList(
                Episode(1, "1 серия SD/HD<br>Hamster",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_black.mirror.s03e01.720p.web-dl.hamsterstudio.org.a1.22.10.16.mp4"),
                Episode(2, "2 серия SD/HD<br>Hamster",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_black.mirror.s03e02.720p.web-dl.hamsterstudio.org.a1.22.10.16.mp4"),
                Episode(3, "3 серия SD/HD<br>Hamster",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_black.mirror.s03e03.720p.web-dl.hamsterstudio.org.a1.22.10.16.mp4"),
                Episode(4, "4 серия SD/HD<br>Hamster",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e04.720p.WEB-DL.HamsterStudio.org.a1.22.10.16.mp4"),
                Episode(5, "5 серия SD/HD<br>Hamster",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e05.720p.WEB-DL.HamsterStudio.org.a1.22.10.16.mp4"),
                Episode(6, "6 серия SD<br>Hamster",
                        "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e06.WEB-DLRip.HamsterStudio.org.a1.22.10.16.mp4"))

        assertEquals(expected, list)

    }
}