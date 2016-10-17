package in.artsaf.seriesapp.seasonvar;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import in.artsaf.seriesapp.seasonvar.PlaylistItem;
import in.artsaf.seriesapp.seasonvar.SeasonvarLoader;
import in.artsaf.seriesapp.seasonvar.SeasonvarParser;

public class LoadEpisodesTask extends AsyncTask<String, Void, ArrayList<PlaylistItem>> {

    public interface LoadEpisodesTaskHandler {
        void onEpisodesLoaded(ArrayList<PlaylistItem> playlistItems);
    }

    private final LoadEpisodesTaskHandler handler;
    private final SeasonvarLoader loader = new SeasonvarLoader();

    public LoadEpisodesTask(LoadEpisodesTaskHandler handler) {
        this.handler = handler;
    }

    @Override
    protected ArrayList<PlaylistItem> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String url = params[0];

        int seasonId = SeasonvarParser.parseSeasonId(url);

        String html = null;
        try {
            html = loader.loadPageByUrl(url);
            String sessionKey = SeasonvarParser.parseSessionKey(html);

            return loader.loadPlaylist(sessionKey, seasonId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<PlaylistItem> playlistItems) {
        handler.onEpisodesLoaded(playlistItems);
    }
}
