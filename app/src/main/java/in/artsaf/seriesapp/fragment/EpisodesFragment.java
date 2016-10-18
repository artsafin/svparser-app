package in.artsaf.seriesapp.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.seasonvar.LoadEpisodesTask;
import in.artsaf.seriesapp.seasonvar.PlaylistItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EpisodesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EpisodesFragment extends Fragment implements LoadEpisodesTask.LoadEpisodesTaskHandler, AdapterView.OnItemClickListener {
    public static final String ARG_URL = "url";

    private String url;

    private ProgressDialog progressDialog;
    private ListView listView;

    private ArrayAdapter<PlaylistItem> adapter;

    public EpisodesFragment() {
        // Required empty public constructor
    }

    public static EpisodesFragment newInstance(String url) {
        EpisodesFragment fragment = new EpisodesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);

            progressDialog.show();

            LoadEpisodesTask loadEpisodesTask = new LoadEpisodesTask(this);
            loadEpisodesTask.execute(url);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        View root =  inflater.inflate(R.layout.fragment_episodes, container, false);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView = (ListView) root.findViewById(R.id.listViewEpisodes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        setHasOptionsMenu(false);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.episodes, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlaylistItem item = (PlaylistItem) parent.getItemAtPosition(position);

        Intent intent = createViewIntent(item);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(getView(), "No activity can open this file", Snackbar.LENGTH_SHORT);
        }
    }

    private Intent createViewIntent(PlaylistItem item)
    {
        Uri uri = Uri.parse(item.file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);

        return intent;
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

    @Override
    public void onEpisodesLoaded(ArrayList<PlaylistItem> playlistItems) {
        progressDialog.hide();

        adapter.clear();
        adapter.addAll(playlistItems);
    }
}
