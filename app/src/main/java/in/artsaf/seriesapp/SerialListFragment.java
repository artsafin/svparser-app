package in.artsaf.seriesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import in.artsaf.seriesapp.dto.Serial;

import static in.artsaf.seriesapp.api.SeriesProviderContract.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class SerialListFragment extends Fragment implements AdapterView.OnItemClickListener {
    interface SerialListFragmentHandler {
        void onSerialClick(Serial serial);
    }

    private SerialListFragmentHandler eventHandler;
    private SimpleCursorAdapter adapter;

    LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity(),
                    Serials.urlSerials(null),
                    Serials.ListProjection.FIELDS,
                    null,
                    null,
                    Serials.ListProjection.SORT_ORDER
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

    public SerialListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SerialListFragmentHandler) {
            eventHandler = (SerialListFragmentHandler) context;
        } else {
            throw new UnsupportedOperationException("Activity must implement " + SerialListFragmentHandler.class.getSimpleName());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        setHasOptionsMenu(true);

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{Serials.NAME, Serials.IMAGE},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );

        ListView listView = (ListView) getView().findViewById(R.id.serial_list_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, loaderCallbacks);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_serial_list, container, false);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            eventHandler.onSerialClick(Serials.ListProjection.toValueObject(cursor));
        }
    }
}
