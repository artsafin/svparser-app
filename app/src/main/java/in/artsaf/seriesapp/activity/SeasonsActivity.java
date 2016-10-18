package in.artsaf.seriesapp.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.fragment.SeasonsFragment;
import in.artsaf.seriesapp.dto.Season;
import in.artsaf.seriesapp.dto.Serial;

public class SeasonsActivity extends AppCompatActivity
    implements SeasonsFragment.SeasonsFragmentHandler
{
    private static final String TAG = SeasonsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationViewHelper.createDefault((NavigationView) findViewById(R.id.nav_view), drawer, this);

        if (findViewById(R.id.activity_seasons_content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            Intent intent = getIntent();
            if (intent != null) {
                SeasonsFragment fragment = SeasonsFragment.newInstance(intent.getExtras());
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.activity_seasons_content, fragment);
                ft.commit();

                Serial serial = (Serial) intent.getSerializableExtra(SeasonsFragment.EXTRA_SERIAL);
                if (serial != null && serial.name != null) {
                    setTitle(serial.name);
                }
            }
        }
    }

    @Override
    public void onSeasonClick(Season season) {
        Log.i(TAG, season.toString());
    }
}
