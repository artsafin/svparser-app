package in.artsaf.seriesapp.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.dto.Serial;

import static in.artsaf.seriesapp.api.SeriesProviderContract.*;


public class Database extends SQLiteOpenHelper {
    private static final String TAG = Database.class.getSimpleName();

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "seriesapp.db";
    public static final String SERIALS_TABLE = "serials";
    public static final String SEASONS_TABLE = "seasons";

    public interface Loader<T> {
        List<T> load();
    }

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
        {"id":14242,"commonName":"Теория большого взрыва",
        "name":"Сериал Теория большого взрыва/The Big Bang Theory 10 сезон",
        "url":"/serial-14242-Teoriya_bol_shogo_vzryva-10-season.html",
        "description":"Наука и только наука! Вот ...",
        "genres":["комедия"],
        "year":"2016",
        "img":"http://cdn.seasonvar.ru/oblojka/14242.jpg",
        "originalName":"The Big Bang Theory",
        "numSeasons":10}
         */

        db.execSQL("CREATE TABLE serials(" +
                Serials._ID + " INTEGER PRIMARY KEY, " +
                Serials.NAME + " TEXT, " +
                Serials.IMAGE + " TEXT, " +
                Serials.DESCRIPTION + " TEXT, " +
                Serials.GENRES + " TEXT, " +
                Serials.IMG + " TEXT, " +
                Serials.ORIGINAL_NAME + " TEXT, " +
                Serials.NUM_SEASONS + " TEXT, " +
                Serials.UPDATE_TS + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE seasons(" +
                Seasons._ID + " INTEGER PRIMARY KEY, " +
                Seasons.NAME + " TEXT, " +
                Seasons.URL + " TEXT, " +
                Seasons.YEAR + " TEXT, " +
                Seasons.UPDATE_TS + " TEXT" +
                ")");

        Log.d(TAG, "onCreate: Created databases");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
    }

    public Serial findSerialById(long serialId) {
        Cursor c = getReadableDatabase().query(SERIALS_TABLE, new String[]{
                Serials._ID, Serials.NAME, Serials.IMAGE
        }, Serials._ID + "=?", new String[]{String.valueOf(serialId)}, null, null, null, "LIMIT 1");

        if (c.moveToNext()) {
            Serial s = new Serial(Long.parseLong(c.getString(0)), c.getString(1), c.getString(2));
            Log.d(TAG, "findSerialById: found record: " + s.toString());
            return s;
        }

        return null;
    }

    public Season findSeasonById(long seasonId) {
        Cursor c = getReadableDatabase().query(SEASONS_TABLE, new String[]{
                Seasons._ID, Seasons.NAME, Seasons.URL, Seasons.YEAR
        }, Seasons._ID + "=?", new String[]{String.valueOf(seasonId)}, null, null, null, "LIMIT 1");

        if (c.moveToNext()) {
            Season s = new Season(Long.parseLong(c.getString(0)), c.getString(1), c.getString(2), c.getString(3));
            Log.d(TAG, "findSeasonById: found record: " + s.toString());
            return s;
        }

        return null;
    }

    public Cursor serials(String search, String[] projection, String selection, String[] selectionArgs, String sortOrder, Loader<Serial> loader) {
        Cursor c = querySerials(search, projection, selection, selectionArgs, sortOrder);
        if (c == null) {
            Log.d(TAG, "serials: fetch from api");
            loadAndInsertSerials(loader);
            c = querySerials(search, projection, selection, selectionArgs, sortOrder);
        } else {
            Log.d(TAG, "serials: found in db!");
        }
        return c;
    }

    @Nullable
    private Cursor querySerials(String search, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (search == null || search.length() == 0) {
            Cursor c = getReadableDatabase().query(SERIALS_TABLE, projection, "", null, null, null, sortOrder);
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } else {
            Cursor c = getReadableDatabase().query(SERIALS_TABLE, projection, Serials.NAME + " like '%?%'", new String[]{search}, null, null, sortOrder);
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        }
    }

    private void loadAndInsertSerials(Loader<Serial> loader) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (Serial row : loader.load()) {
                ContentValues values = new ContentValues();
                values.put(Serials.NAME, row.name);
                values.put(Serials.IMAGE, row.image);
                values.put(Serials.UPDATE_TS, SimpleDateFormat.getInstance().format(new Date()));
                db.insert(SERIALS_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor seasons(long serialId, String[] projection, String selection, String[] selectionArgs, String sortOrder, Loader<Season> loader) {
        Cursor c = querySeasons(serialId, projection);
        if (c == null) {
            Log.d(TAG, "seasons: fetching from api");
            loadAndInsertSeasons(loader);
            c = querySeasons(serialId, projection);
        } else {
            Log.d(TAG, "seasons: found in db!");
        }

        return c;
    }

    @Nullable
    private Cursor querySeasons(long serialId, String[] projection) {
        Cursor c = getReadableDatabase().query(SEASONS_TABLE, projection, "", null, null, null, null);
        if (c.getCount() > 0) {
            return c;
        }

        return null;
    }

    private void loadAndInsertSeasons(Loader<Season> loader) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (Season row : loader.load()) {
                ContentValues values = new ContentValues();
                values.put(Seasons._ID, row.id);
                values.put(Seasons.NAME, row.name);
                values.put(Seasons.URL, row.url);
                values.put(Seasons.YEAR, row.year);
                values.put(Seasons.UPDATE_TS, SimpleDateFormat.getInstance().format(new Date()));
                db.insert(SEASONS_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
