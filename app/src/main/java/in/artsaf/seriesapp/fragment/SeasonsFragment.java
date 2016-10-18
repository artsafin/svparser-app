package in.artsaf.seriesapp.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.dto.Serial;

import static in.artsaf.seriesapp.data.SeriesProviderContract.*;


public class SeasonsFragment extends Fragment implements AdapterView.OnItemClickListener {
    public interface SeasonsFragmentHandler {
        void onSeasonClick(Season season);
    }

    private static final String TAG = SeasonsFragment.class.getSimpleName();

    public static final String EXTRA_SERIAL = "serial";

    private Serial serial;
    private SeasonsFragmentHandler eventHandler;
    private SimpleCursorAdapter adapter;

    private static final int LOADER_ID = 1;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            long serialId = args.getLong(EXTRA_SERIAL);
            Log.i(TAG + "/loader", "Loading serialId " + String.valueOf(serialId));
            return new CursorLoader(
                    getActivity(),
                    Seasons.urlSeasonsBySerial(serialId),
                    Seasons.ListProjection.FIELDS,
                    null,
                    null,
                    Seasons.ListProjection.SORT_ORDER
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    };

    public SeasonsFragment() { }

    public static SeasonsFragment newInstance(Bundle args) {
        SeasonsFragment fragment = new SeasonsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serial = (Serial) getArguments().getSerializable(EXTRA_SERIAL);

            Log.i(TAG, "onCreate " + ((serial == null) ? "<null>" : serial.toString()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seasons, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{Seasons.NAME},
                new int[]{android.R.id.text1},
                0
        );

        ListView listview = (ListView) getView().findViewById(R.id.seasons_listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        Log.i(TAG, "onActivityCreated " + String.valueOf(serial.id) + " " + adapter.toString());
        Bundle loaderArgs = new Bundle();
        loaderArgs.putLong(EXTRA_SERIAL, serial.id);
        getLoaderManager().initLoader(LOADER_ID, loaderArgs, loaderCallbacks);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SeasonsFragmentHandler) {
            eventHandler = (SeasonsFragmentHandler) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " + SeasonsFragmentHandler.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventHandler = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            eventHandler.onSeasonClick(Seasons.ListProjection.toValueObject(cursor));
        }
    }
}
