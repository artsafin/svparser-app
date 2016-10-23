package in.artsaf.seriesapp.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.data.SeriesProviderContract;
import in.artsaf.seriesapp.dto.Episode;
import in.artsaf.seriesapp.dto.Playlist;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.seasonvar.PlaylistItem;

public class PlaylistFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = PlaylistFragment.class.getSimpleName();

    public static final String EXTRA_PLAYLIST = "playlist";

    private Playlist playlist;

    private EpisodesFragment.EpisodesFragmentHandler eventHandler;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    public static PlaylistFragment newInstance(Playlist pls) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PLAYLIST, pls);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EpisodesFragment.EpisodesFragmentHandler) {
            eventHandler = (EpisodesFragment.EpisodesFragmentHandler) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " + EpisodesFragment.EpisodesFragmentHandler.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            playlist = (Playlist) getArguments().getSerializable(EXTRA_PLAYLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_episodes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<Episode> adapter = new ArrayAdapter<Episode>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                playlist
        );

        ListView listView = (ListView) getView().findViewById(R.id.episodes_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) parent.getItemAtPosition(position);

        Episode ep = SeriesProviderContract.Episodes.ListProjection.toValueObject(c);

        eventHandler.onSingleEpisodeClick(ep);
    }
}
