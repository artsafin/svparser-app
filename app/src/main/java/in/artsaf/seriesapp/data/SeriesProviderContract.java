package in.artsaf.seriesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import in.artsaf.seriesapp.dto.Episode;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.dto.Serial;

public class SeriesProviderContract {
    public static final String AUTHORITY = "in.artsaf.seriesapp.data.api.provider.serialapi";

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
                return new Serial(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
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
        public static final String SERIAL_ID = "serial_id";

        public static Uri urlSeasonsBySerial(long id) {
            return ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id).build();
        }

        public static class ListProjection {
            public static final String[] FIELDS = {_ID, SERIAL_ID, NAME, URL, YEAR};
            public static final String SORT_ORDER = NAME + " ASC";

            public static Season toValueObject(Cursor cursor) {
                return new Season(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
            }
        }
    }

    public static class Episodes {
        static final String PATH = "episodes";
        static final int MATCHER_ID = 2;
        static final String MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH;

        public static final String _ID = "id";
        public static final String COMMENT = "comment";
        public static final String FILE = "file";

        public static Uri urlEpisodesBySeason(long id) {
            return ContentUris.appendId(baseUri.buildUpon().appendPath(PATH), id).build();
        }

        public static class ListProjection {
            public static final String[] FIELDS = {_ID, COMMENT, FILE};

            public static Episode toValueObject(Cursor cursor) {
                return new Episode(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
            }
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
