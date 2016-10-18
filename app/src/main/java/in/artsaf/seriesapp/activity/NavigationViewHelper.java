package in.artsaf.seriesapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import in.artsaf.seriesapp.R;

public class NavigationViewHelper implements NavigationView.OnNavigationItemSelectedListener {
    private final NavigationView navigationView;
    private final DrawerLayout drawer;

    public interface NavigationViewHandler {
        void handle(MenuItem item);
    }

    private Map<Integer, NavigationViewHandler> handlers = new HashMap<>();

    public static NavigationViewHelper createDefault(NavigationView navigationView, DrawerLayout drawer, final Context context) {
        NavigationViewHelper h = new NavigationViewHelper(navigationView, drawer);
        h
                .on(R.id.nav_serials, new NavigationViewHandler() {
                    @Override
                    public void handle(MenuItem item) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_SHOW_SERIALS, true);
                        context.startActivity(intent);
                    }
                })
                .on(R.id.nav_by_url, new NavigationViewHandler() {
                    @Override
                    public void handle(MenuItem item) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_SHOW_LOAD_BY_URL, true);
                        context.startActivity(intent);
                    }
                });

        return h;
    }

    public NavigationViewHelper(NavigationView navigationView, DrawerLayout drawer) {
        this.navigationView = navigationView;
        this.drawer = drawer;

        navigationView.setNavigationItemSelectedListener(this);
    }

    public NavigationViewHelper on(int menuItemId, NavigationViewHandler handler) {
        handlers.put(menuItemId, handler);

        return this;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (handlers.containsKey(id)) {
            handlers.get(id).handle(item);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
