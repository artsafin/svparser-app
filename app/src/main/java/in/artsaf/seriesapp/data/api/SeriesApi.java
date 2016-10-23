package in.artsaf.seriesapp.data.api;

import android.net.Uri;

import java.util.List;

import in.artsaf.seriesapp.dto.*;

public interface SeriesApi {
    public static class Contract {
        public static Uri serialsUrl(String baseUrl, String search) {
            Uri.Builder ub = Uri.parse(baseUrl).buildUpon().appendPath("serial");
            if (search != null) {
                ub.appendQueryParameter("search", search);
            }
            return ub.build();
        }

        public static Uri seasonsUrl(String baseUrl, String serialName) {
            return Uri.parse(baseUrl).buildUpon()
                    .appendPath("serial")
                    .appendPath(serialName)
                    .build();
        }

        public static Uri episodesUrl(String baseUrl) {
            return Uri.parse(baseUrl).buildUpon()
                    .appendEncodedPath("episodes/parse")
                    .build();
        }
    }

    List<Serial> serials(String search);

    List<Season> seasons(String serialName);

    Playlist episodes(String seasonHtml);
}
