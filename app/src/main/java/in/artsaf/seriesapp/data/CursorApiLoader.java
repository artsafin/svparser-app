package in.artsaf.seriesapp.data;

import android.database.Cursor;

import java.io.IOException;
import java.util.List;

import in.artsaf.seriesapp.data.api.SeriesApi;
import in.artsaf.seriesapp.dto.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CursorApiLoader {
    private static final String TAG = CursorApiLoader.class.getSimpleName();

    private final SeriesApi api;
    private final Database cache;

    public CursorApiLoader(SeriesApi api, Database cache) {
        this.api = api;
        this.cache = cache;
    }

    public Cursor serials(final String search, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return cache.serials(search, projection, selection, selectionArgs, sortOrder,
                new Database.Loader<Serial>() {
                    @Override
                    public List<Serial> load() {
                        return api.serials(search);
                    }
                });
    }

    public Cursor seasons(final long serialId, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return cache.seasons(serialId, projection, selection, selectionArgs, sortOrder,
                new Database.Loader<Season>() {
                    @Override
                    public List<Season> load() {
                        Serial s = cache.findSerialById(serialId);
                        return api.seasons(s.name);
                    }
                });
    }

    public Cursor episodes(final long seasonId) {
        Season s = cache.findSeasonById(seasonId);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(s.getUrl())
                .addHeader("Cookie", "html5default=1;")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String str = response.body().string();
            List<Episode> episodes = api.episodes(str);
            return new ListCursor(episodes, Episode.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return null;
    }
}
