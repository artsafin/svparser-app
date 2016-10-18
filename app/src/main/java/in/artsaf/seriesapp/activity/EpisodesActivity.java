package in.artsaf.seriesapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.artsaf.seriesapp.fragment.EpisodesFragment;
import in.artsaf.seriesapp.R;

public class EpisodesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        Intent intent = getIntent();

        if (savedInstanceState == null && intent != null && intent.hasExtra(Intent.EXTRA_SUBJECT)) {
            EpisodesFragment fragment = EpisodesFragment.newInstance(intent.getStringExtra(Intent.EXTRA_SUBJECT));

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}