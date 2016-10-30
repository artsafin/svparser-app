package com.artsafin.seriesapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import com.artsafin.seriesapp.R
import com.artsafin.seriesapp.fragment.EpisodesFragment
import com.artsafin.seriesapp.fragment.FavoritesFragment
import com.artsafin.seriesapp.fragment.SerialListFragment

class FavoritesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            val fragment = FavoritesFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_content, fragment)
                    .commit()
        }

        title = getString(R.string.favorites)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        (fragment as? FavoritesFragment)?.clickHandler = { serial ->
            Log.i(javaClass.simpleName, serial.toString())

            val intent = Intent(this, SeasonsActivity::class.java)
                    .with(viewState.append(serial))

            startActivity(intent)
        }
    }
}
