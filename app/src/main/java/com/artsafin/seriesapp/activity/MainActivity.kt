package com.artsafin.seriesapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.artsafin.seriesapp.R

import com.artsafin.seriesapp.fragment.SerialListFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            navigateSerials()
        }
    }

    override fun onResume() {
        super.onResume()

        navigationView.setCheckedItem(R.id.nav_serials)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        (fragment as? SerialListFragment)?.clickHandler = { serial ->
            Log.i("onSerialClick: ", serial.toString())

            val intent = Intent(this, SeasonsActivity::class.java)
                            .with(viewState.append(serial))

            startActivity(intent)
        }
    }
}
