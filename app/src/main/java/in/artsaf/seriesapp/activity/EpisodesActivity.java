package in.artsaf.seriesapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.MimeTypeMap;

import in.artsaf.seriesapp.dto.Episode;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.fragment.EpisodesFragment;
import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.fragment.PlaylistFragment;

public class EpisodesActivity extends AppCompatActivity implements EpisodesFragment.EpisodesFragmentHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationViewHelper.createDefault((NavigationView) findViewById(R.id.nav_view), drawer, this);

        Intent intent = getIntent();

        if (savedInstanceState == null && intent != null && intent.hasExtra(EpisodesFragment.EXTRA_SEASON)) {
            Season s = (Season) intent.getSerializableExtra(EpisodesFragment.EXTRA_SEASON);
            setTitle(s.name);
            EpisodesFragment fragment = EpisodesFragment.newInstance(s);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_episodes_content, fragment)
                    .commit();
        }
    }

    private Intent createViewIntent(Episode item)
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
    public void onSingleEpisodeClick(Episode ep) {
        Intent intent = createViewIntent(ep);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(R.id.activity_episodes_content), R.string.no_video_activity, Snackbar.LENGTH_LONG);
        }
    }

//    @Override
    public void onMultiEpisodeClick(Episode ep) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_episodes_content, PlaylistFragment.newInstance(ep.playlist))
                .commit();
    }
}
