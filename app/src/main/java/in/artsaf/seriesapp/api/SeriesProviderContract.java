package in.artsaf.seriesapp.api;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import in.artsaf.seriesapp.dto.Serial;

public class SeriesProviderContract {
    public static final String AUTHORITY = "in.artsaf.seriesapp.api.provider.serialapi";

    private static final Uri baseUri = Uri.parse("content://" + AUTHORITY);

    public static class Serials {
        static final String PATH = "serials";
        static final int MATCHER_ID = 0;
        static final String MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH;

        public static final String NAME = "name";
        public static final String IMAGE = "image";
        public static final String _ID = "_id";
        public static final String DESCRIPTION = "description";
        public static final String GENRES = "genres";
        public static final String IMG = "img";
        public static final String ORIGINAL_NAME = "original_name";
        public static final String NUM_SEASONS = "num_seasons";
        public static final String UPDATE_TS = "update_ts";

        public static Uri urlSerials(String search) {
            Uri.Builder builder = baseUri.buildUpon().appendPath(PATH);

            if (search != null) {
                builder.appendQueryParameter("search", search);
            }

            return builder.build();
        }

        public static class ListProjection {
            public static final String[] FIELDS = {_ID, NAME, IMAGE};
            public static final String SORT_ORDER = NAME + " ASC";

            public static Serial toValueObject(Cursor cursor) {
                return new Serial(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            }
        }
    }

    public static class Seasons {
        static final String PATH = "seasons";
        static final int MATCHER_ID = 1;
        static final String MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH;

        public static final String NAME = "name";
        public static final String URL = "url";
        public static final String YEAR = "year";
        public static final String UPDATE_TS = "update_ts";
        public static final String _ID = "_id";

        public static Uri urlSeasonsBySerial(long id) {
            return ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id).build();
        }
    }

    public static class Episodes {
        static final String PATH = "episodes";
        static final int MATCHER_ID = 2;
        static final String MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH;

        public static Uri urlEpisodesBySeason(long id) {
            return ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id).build();
        }
    }

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(AUTHORITY, Serials.PATH, Serials.MATCHER_ID);
        matcher.addURI(AUTHORITY, Seasons.PATH + "/#", Seasons.MATCHER_ID);
        matcher.addURI(AUTHORITY, Episodes.PATH + "/#", Episodes.MATCHER_ID);
    }

    public static int matchUri(Uri uri) {
        return matcher.match(uri);
    }
}
