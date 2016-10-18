package in.artsaf.seriesapp.data.api;

import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static in.artsaf.seriesapp.data.SeriesProviderContract.*;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SeriesProviderContractTest {

    @Test
    public void serialsNullSearch() {
        Uri uri = Serials.urlSerials(null);

        assertEquals("content://" + AUTHORITY + "/serials", uri.toString());
    }

    @Test
    public void serialsNonNullSearch() {
        Uri uri = Serials.urlSerials("hello world 123");

        assertEquals("content://" + AUTHORITY + "/serials?search=hello%20world%20123", uri.toString());
    }

    @Test
    public void seasonsUrl() {
        Uri uri = Seasons.urlSeasonsBySerial(100500);

        assertEquals("content://" + AUTHORITY + "/seasons/" + String.valueOf(100500), uri.toString());
    }

    @Test
    public void episodesUrl() {
        Uri uri = Episodes.urlEpisodesBySeason(200600);

        assertEquals("content://" + AUTHORITY + "/episodes/" + String.valueOf(200600), uri.toString());
    }
}