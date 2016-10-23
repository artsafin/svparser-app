package in.artsaf.seriesapp.data.api;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.artsaf.seriesapp.dto.Episode;
import in.artsaf.seriesapp.dto.Playlist;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.dto.Serial;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpSeriesApi implements SeriesApi {

    private static final String DEFAULT_TRANSLATION_KEY = "default";

    protected interface RequestExecutor {
        Response execute(Request req) throws IOException;
    }

    private static final String TAG = HttpSeriesApi.class.getSimpleName();
    private static final String BASE_URL = "http://138.68.84.5/api";

    private final Gson gson = new Gson();
    protected RequestExecutor executor = new RequestExecutor() {
        private final OkHttpClient client = new OkHttpClient();

        @Override
        public Response execute(Request req) throws IOException {
            return client.newCall(req).execute();
        }
    };

    private Request.Builder defaultReq(Uri url) {
        return new Request.Builder()
                .header("Accept", "application/json")
                .url(url.toString())
                ;
    }

    @Override
    public List<Serial> serials(String search) {
        try {
            Request req = defaultReq(Contract.serialsUrl(BASE_URL, search)).build();
            Response resp = null;
            resp = executor.execute(req);
            return gson.fromJson(resp.body().string(), new TypeToken<ArrayList<Serial>>() {}.getType());
        } catch (IOException e) {
            Log.e(TAG, "serials IOException", e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Season> seasons(String serialName) {
        try {
            Request req = defaultReq(Contract.seasonsUrl(BASE_URL, serialName)).build();
            Response resp = null;
            resp = executor.execute(req);
            String body = resp.body().string();

            Log.d(TAG, "seasons response: " + body);

            return gson.fromJson(body, new TypeToken<ArrayList<Season>>() {}.getType());
        } catch (IOException e) {
            Log.e(TAG, "seasons IOException", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Playlist episodes(String seasonHtml) {
        try {
            Request req = defaultReq(Contract.episodesUrl(BASE_URL))
                    .header("Content-Type", "text/html; charset=utf-8")
                    .post(RequestBody.create(MediaType.parse("text/html; charset=utf-8"), seasonHtml))
                    .build();
            Response resp = null;
            resp = executor.execute(req);
            String body = resp.body().string();

            Log.d(TAG, "episodes response: " + body);

            Map<String, Map<String, Playlist>> map;
            map = gson.fromJson(body, new TypeToken<Map<String, Map<String, Playlist>>>() {}.getType());

            return map.get(DEFAULT_TRANSLATION_KEY).get("playlist");
        } catch (JsonSyntaxException exc) {

        } catch (IOException e) {
            Log.e(TAG, "episodes IOException", e);
            e.printStackTrace();
        }

        return null;
    }
}
