package com.artsafin.seriesapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.data.contract.Serials
import com.artsafin.seriesapp.dto.Serial
import com.artsafin.seriesapp.fragment.SerialListFragment

open class BaseActivity : AppCompatActivity() {
    private val TAG = BaseActivity::class.java.simpleName
    lateinit protected var toolbar: Toolbar
    lateinit protected var viewState: ViewState
    lateinit protected var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_common)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                                           R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener { item ->
            val result = when (item.itemId) {
                R.id.nav_serials -> navigateSerials()
                R.id.nav_favorites -> navigateFavorites()
                R.id.nav_recently_watched -> navigateRecentlyWatched()
                else -> false
            }

            if (result) {
                drawer.closeDrawer(GravityCompat.START)
            }

            result
        }

        viewState = GlobalViewstate.viewState
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        viewState.serial?.run {
            menu?.findItem(R.id.menu_serial_add_favorite)?.isVisible = !favorite
            menu?.findItem(R.id.menu_serial_remove_favorite)?.isVisible = favorite
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private fun markSerialFavorite(serial: Serial, fav: Boolean) {
        with (Serials.Fav.updateQuery(serial, fav)) {
            if (contentResolver.update(first, second.values, second.where, second.whereArgs) > 0) {
                serial.favorite = fav
                invalidateOptionsMenu()
                GlobalViewstate.serial.dirty()

                Snackbar.make(this@BaseActivity.findViewById(android.R.id.content),
                              getString(if (fav) R.string.added_favorites_ else R.string.removed_favorites_, serial.name),
                              Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_serial_add_favorite -> viewState.serial?.run { markSerialFavorite(this, true); true } ?: false
        R.id.menu_serial_remove_favorite -> viewState.serial?.run { markSerialFavorite(this, false); true } ?: false
        else -> false
    }

    protected fun navigateSerials(): Boolean {
        title = getString(R.string.tv_series)

        if (this is MainActivity) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_content, SerialListFragment())
                    .commit()
        } else {
            val intent = Intent(this, MainActivity::class.java).with(viewState)
            startActivity(intent)
        }

        return true
    }

    protected fun navigateFavorites(): Boolean {
        val intent = Intent(this, FavoritesActivity::class.java).with(viewState)
        startActivity(intent)

        return true
    }

    protected fun navigateRecentlyWatched(): Boolean {
        val intent = Intent(this, RecentlyWatchedActivity::class.java).with(viewState)
        startActivity(intent)

        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
