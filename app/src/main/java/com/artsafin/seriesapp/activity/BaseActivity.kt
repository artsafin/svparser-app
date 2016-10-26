package com.artsafin.seriesapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.fragment.SerialListFragment

open class BaseActivity(val intentClass: Class<out Context>?) : AppCompatActivity() {
    val EXTRA_NAV_SERIALS = "nav_serials"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_common)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener { item ->
            val result = when (item.itemId) {
                R.id.nav_serials -> navigateSerials()
                R.id.nav_favorites -> navigateFavorites()
                else -> false
            }

            if (result) {
                drawer.closeDrawer(GravityCompat.START)
            }

            result
        }
    }

    protected fun navigateSerials(): Boolean {
        title = getString(R.string.title_activity_serial_list)

        if (intentClass == null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.activity_content, SerialListFragment())
            ft.commit()
        } else {
            val intent = Intent(this, intentClass)
            intent.putExtra(EXTRA_NAV_SERIALS, true)
            startActivity(intent)
        }

        return true
    }

    protected fun navigateFavorites(): Boolean {
        title = getString(R.string.title_activity_favorites)

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
