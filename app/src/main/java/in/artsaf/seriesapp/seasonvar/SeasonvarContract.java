package in.artsaf.seriesapp.seasonvar;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;

public class SeasonvarContract
{
    public static final String AUTHORITY = "in.artsaf.seriesapp.provider.seasonvar";
    public static final String PATH_SEASON = "season";

    public static final Uri baseUri = Uri.parse("content://" + AUTHORITY);

    public static final int MATCHER_SEASON = 0;
    public static final int MATCHER_EPISODES = 1;
    public static final String MIME_TYPE_SEASON_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_SEASON;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(AUTHORITY, PATH_SEASON, MATCHER_SEASON);
        matcher.addURI(AUTHORITY, PATH_SEASON + "/*", MATCHER_EPISODES);
    }

    public static UriMatcher getMatcher()
    {
        return matcher;
    }

    public static Uri buildEpisodesUri(String alias)
    {
        return baseUri.buildUpon()
                .appendPath(PATH_SEASON)
                .appendPath(alias)
                .build();
    }
}
