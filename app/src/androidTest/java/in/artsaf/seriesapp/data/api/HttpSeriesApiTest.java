package in.artsaf.seriesapp.data.api;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import in.artsaf.seriesapp.dto.Episode;
import in.artsaf.seriesapp.dto.Playlist;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.dto.Serial;
import okhttp3.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HttpSeriesApiTest {

    @Before
    public void setUp() {

    }

    @Test
    public void serials() throws Exception {
        SeriesApi api = new TestHttpSeriesApi(new Response.Builder(),
                TestData.serials
        );

        List<Serial> list = api.serials(null);

        List<Serial> expected = Arrays.asList(
                new Serial(0, "Скалолазка", "http://cdn.seasonvar.ru/oblojka/12860.jpg"),
                new Serial(0, "Лалола", "http://cdn.seasonvar.ru/oblojka/1635.jpg")
        );

        assertEquals(expected, list);
    }

    @Test
    public void seasons() throws Exception {
        SeriesApi api = new TestHttpSeriesApi(new Response.Builder(),
                TestData.seasons
        );

        List<Season> list = api.seasons("Whatever");

        List<Season> expected = Arrays.asList(
                new Season(12909, 111, "Сериал Шерлок/Sherlock 4 сезон", "/serial-12909-SHerlok-4-sezon.html", "2016"),
                new Season(8328, 111, "Сериал Шерлок/Sherlock 3 сезон", "/serial-8328-SHerlok-3-sezon.html", "2016")
        );

        assertEquals(expected, list);

    }

    @Test
    public void episodes() throws Exception {
        SeriesApi api = new TestHttpSeriesApi(new Response.Builder(),
                TestData.episodesOne
        );

        Playlist list = api.episodes("Whatever");

        List<Episode> expected = Arrays.asList(
                new Episode("0 серия SD/HD<br>BaibaKo", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e00.White.Christmas.HDTV720p.Rus.Eng.BaibaKo.tv.a1.25.11.15.mp4")
        );

        assertEquals(expected, list);

    }

    @Test
    public void episodesMany() throws Exception {
        SeriesApi api = new TestHttpSeriesApi(new Response.Builder(),
                TestData.episodesMany
        );

        Playlist list = api.episodes("Whatever");

        List<Episode> expected = Arrays.asList(
                new Episode("1 серия SD/HD<br>Hamster", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_black.mirror.s03e01.720p.web-dl.hamsterstudio.org.a1.22.10.16.mp4"),
                new Episode("2 серия SD/HD<br>Hamster", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_black.mirror.s03e02.720p.web-dl.hamsterstudio.org.a1.22.10.16.mp4"),
                new Episode("3 серия SD/HD<br>Hamster", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_black.mirror.s03e03.720p.web-dl.hamsterstudio.org.a1.22.10.16.mp4"),
                new Episode("4 серия SD/HD<br>Hamster", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e04.720p.WEB-DL.HamsterStudio.org.a1.22.10.16.mp4"),
                new Episode("5 серия SD/HD<br>Hamster", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e05.720p.WEB-DL.HamsterStudio.org.a1.22.10.16.mp4"),
                new Episode("6 серия SD<br>Hamster", "http://temp-cdn.datalock.ru/fi2lm/f4843139951e49682b0390d793ce912c/7f_Black.Mirror.s03e06.WEB-DLRip.HamsterStudio.org.a1.22.10.16.mp4")
        );

        assertEquals(expected, list);

    }
}