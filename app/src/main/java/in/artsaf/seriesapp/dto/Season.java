package in.artsaf.seriesapp.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Season {
    private static final String BASE_URL = "http://seasonvar.ru";

    public String url;
    public long id;
    public String name;
    public String year;

    public Season(long id, String name, String url, String year) {
        this.url = url;
        this.id = id;
        this.name = name;
        this.year = year;
    }

    public String getUrl() {
        return BASE_URL + url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Season season = (Season) o;
        return id == season.id &&
                Objects.equal(url, season.url) &&
                Objects.equal(name, season.name) &&
                Objects.equal(year, season.year);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url, id, name, year);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", url)
                .add("id", id)
                .add("name", name)
                .add("year", year)
                .toString();
    }
}
