package in.artsaf.seriesapp.api;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;


public class SeriesProvider extends ContentProvider {
    CursorApiLoader api;

    @Override
    public boolean onCreate() {
        api = new CursorApiLoader(new HttpSeriesApi(), new Database(getContext()));

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (SeriesProviderContract.matchUri(uri)) {
            case SeriesProviderContract.Serials.MATCHER_ID:
                return api.serials(uri.getQueryParameter("search"), projection, selection, selectionArgs, sortOrder);
            case SeriesProviderContract.Seasons.MATCHER_ID:
                long seasonId = ContentUris.parseId(uri);
                return api.seasons(seasonId, projection, selection, selectionArgs, sortOrder);
            case SeriesProviderContract.Episodes.MATCHER_ID:
                long epId = ContentUris.parseId(uri);
                return api.episodes(epId);
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (SeriesProviderContract.matchUri(uri)) {
            case SeriesProviderContract.Serials.MATCHER_ID:
                return SeriesProviderContract.Serials.MIME_TYPE_DIR;
            case SeriesProviderContract.Seasons.MATCHER_ID:
                return SeriesProviderContract.Seasons.MIME_TYPE_DIR;
            case SeriesProviderContract.Episodes.MATCHER_ID:
                return SeriesProviderContract.Episodes.MIME_TYPE_DIR;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Serial API provider is read only");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Serial API provider is read only");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Serial API provider is read only");
    }
}
