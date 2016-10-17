package in.artsaf.seriesapp.seasonvar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeasonvarParser
{
    public static String parseSessionKey(String input)
    {
        Pattern pattern = Pattern.compile("/([a-z0-9]{33})/");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static int parseSeasonId(String alias)
    {
        // serials-130-Proslushka-1-sezon.html
        Pattern pattern = Pattern.compile("serial-(\\d+)-");
        Matcher matcher = pattern.matcher(alias);
        if (matcher.find()) {
            String seasonId = matcher.group(1);
            return Integer.parseInt(seasonId);
        }

        return 0;
    }
}
