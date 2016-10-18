package in.artsaf.seriesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import in.artsaf.seriesapp.fragment.LoadByUrlFragment;
import in.artsaf.seriesapp.R;
import in.artsaf.seriesapp.fragment.SeasonsFragment;
import in.artsaf.seriesapp.fragment.SerialListFragment;
import in.artsaf.seriesapp.dto.Serial;

public class MainActivity extends AppCompatActivity
        implements LoadByUrlFragment.LoadByUrlInteractionListener,
        SerialListFragment.SerialListFragmentHandler {
    public static final String EXTRA_SHOW_SERIALS = "show_serials";
    public static final String EXTRA_SHOW_LOAD_BY_URL = "show_load_by_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        new NavigationViewHelper((NavigationView) findViewById(R.id.nav_view), drawer)
                .on(R.id.nav_serials, new NavigationViewHelper.NavigationViewHandler() {
                    @Override
                    public void handle(MenuItem item) {
                        showSerials();
                    }
                })
                .on(R.id.nav_by_url, new NavigationViewHelper.NavigationViewHandler() {
                    @Override
                    public void handle(MenuItem item) {
                        showLoadByUrl();
                    }
                });

        showLoadByUrl();
    }

    private void showSerials() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, new SerialListFragment());
        ft.commit();

        getSupportActionBar().setTitle(R.string.title_activity_serial_list);
    }

    private void showLoadByUrl() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, LoadByUrlFragment.newInstance());
        ft.commit();

        getSupportActionBar().setTitle(R.string.title_activity_main);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadByUrlInteraction(String url) {
        Intent intent = new Intent(this, EpisodesActivity.class);
        intent.putExtra(Intent.EXTRA_SUBJECT, url);
        startActivity(intent);
    }

    @Override
    public void onSerialClick(Serial serial) {
        Log.i("MainActivity", serial.toString());

        Intent intent = new Intent(this, SeasonsActivity.class);
        intent.putExtra(SeasonsFragment.EXTRA_SERIAL, serial);
        startActivity(intent);
    }
}
