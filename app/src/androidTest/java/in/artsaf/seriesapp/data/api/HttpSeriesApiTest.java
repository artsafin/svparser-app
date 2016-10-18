package in.artsaf.seriesapp.data.api;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

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
}