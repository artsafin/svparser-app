package in.artsaf.seriesapp.seasonvar;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeasonvarLoader {
    private static final String PLAYLIST_PAGE = "http://seasonvar.ru/playls2/%s/trans/%d/list.xml?time=%d";
    private static final String SV_PAGE = "http://seasonvar.ru/%s.html";

    public String loadPageByAlias(String alias) throws IOException {
        String url = String.format(SV_PAGE, alias);

        return loadPageByUrl(url);
    }

    public String loadPageByUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", "html5default=1;")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public ArrayList<PlaylistItem> loadPlaylist(String sessionKey, int seasonId) throws IOException, JSONException {
        String url = String.format(Locale.getDefault(), PLAYLIST_PAGE, sessionKey, seasonId, new Date().getTime());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        JSONObject root = new JSONObject(response.body().string());
        if (!root.has("playlist")) {
            throw new JSONException("JSON parse: playlist not found");
        }

        JSONArray playlist = root.getJSONArray("playlist");
        ArrayList<PlaylistItem> items = new ArrayList<PlaylistItem>(playlist.length());

        for (int i=0; i<playlist.length(); i++) {
            JSONObject item = playlist.getJSONObject(i);
            if (!item.has("file") || !item.has("comment")) {
                continue;
            }
            items.add(new PlaylistItem(item.getString("file"), item.getString("comment")));
        }

        return items;
    }
}
