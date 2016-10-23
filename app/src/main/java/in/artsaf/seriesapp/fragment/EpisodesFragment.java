package in.artsaf.seriesapp.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.data.CursorApiLoader;
import in.artsaf.seriesapp.data.SeriesProviderContract;
import in.artsaf.seriesapp.dto.Episode;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.seasonvar.PlaylistItem;

public class EpisodesFragment extends Fragment implements AdapterView.OnItemClickListener {
    public interface EpisodesFragmentHandler {
        void onSingleEpisodeClick(Episode ep);
//        void onMultiEpisodeClick(Episode ep);
    }

    private static final String TAG = EpisodesFragment.class.getSimpleName();

    public static final String EXTRA_SEASON = "season";

    private Season season;

    private ProgressDialog progressDialog;
    private ListView listView;

    private EpisodesFragmentHandler eventHandler;
    private SimpleCursorAdapter adapter;

    private static final int LOADER_ID = 2;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity(),
                    SeriesProviderContract.Episodes.urlEpisodesBySeason(season.id),
                    SeriesProviderContract.Episodes.ListProjection.FIELDS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.swapCursor(data);

            if (progressDialog != null) {
                progressDialog.hide();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);

            if (progressDialog != null) {
                progressDialog.hide();
            }
        }
    };

    public EpisodesFragment() {
        // Required empty public constructor
    }

    public static EpisodesFragment newInstance(Season season) {
        EpisodesFragment fragment = new EpisodesFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SEASON, season);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EpisodesFragmentHandler) {
            eventHandler = (EpisodesFragmentHandler) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " + EpisodesFragmentHandler.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            season = (Season) getArguments().getSerializable(EXTRA_SEASON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        View root =  inflater.inflate(R.layout.fragment_episodes, container, false);

        setHasOptionsMenu(false);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SeriesProviderContract.Episodes.COMMENT},
                new int[]{android.R.id.text1},
                0
        );

        listView = (ListView) getView().findViewById(R.id.episodes_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.episodes_menu, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) parent.getItemAtPosition(position);

        Episode ep = SeriesProviderContract.Episodes.ListProjection.toValueObject(c);

        eventHandler.onSingleEpisodeClick(ep);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                for (int i=0;i<listView.getCount();i++) {
                    listView.setItemChecked(i, true);
                }
                return true;
            case R.id.action_share:
                ArrayList<Uri> checkedUrls = getCheckedUrls();
                if (checkedUrls.size() == 0) {
                    return true;
                }
                return true;
        }

        return false;
    }

    private ArrayList<Uri> getCheckedUrls() {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i=0;i<listView.getCount();i++) {
            if (!listView.isItemChecked(i)) {
                continue;
            }
            PlaylistItem item = (PlaylistItem) listView.getItemAtPosition(i);
            uris.add(Uri.parse(item.file));
        }

        return uris;
    }
}
