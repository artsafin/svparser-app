package in.artsaf.seriesapp.seasonvar;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SeasonvarContentProvider extends ContentProvider
{
    private final SeasonvarLoader loader = new SeasonvarLoader();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        UriMatcher matcher = SeasonvarContract.getMatcher();

        switch (matcher.match(uri)) {
            case SeasonvarContract.MATCHER_EPISODES:
                String alias = uri.getLastPathSegment();

                MatrixCursor cursor = new MatrixCursor(new String[] {"file", "comment"});
                ArrayList<PlaylistItem> episodes = loadEpisodes(alias);
                for (PlaylistItem item: episodes) {
                    cursor.addRow(new String[] {item.file, item.comment});
                }

                return cursor;
        }

        return null;
    }

    private ArrayList<PlaylistItem> loadEpisodes(String alias) {
        try {
            int seasonId = SeasonvarParser.parseSeasonId(alias);

            String html = loader.loadPageByUrl(alias);
            String sessionKey = SeasonvarParser.parseSessionKey(html);

            return loader.loadPlaylist(sessionKey, seasonId);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<PlaylistItem>();
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        UriMatcher matcher = SeasonvarContract.getMatcher();

        switch (matcher.match(uri)) {
            case SeasonvarContract.MATCHER_EPISODES:
                return SeasonvarContract.MIME_TYPE_SEASON_DIR;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Cannot insert to series provider");
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("Cannot delete to series provider");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException("Cannot update to series provider");
    }
}
