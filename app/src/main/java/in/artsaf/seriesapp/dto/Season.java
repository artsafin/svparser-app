package in.artsaf.seriesapp.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class Season implements Serializable {
    private static final String BASE_URL = "http://seasonvar.ru";

    public long id;
    public long serialId;
    public String url;
    public String name;
    public String year;

    public Season(long id, long serialId, String name, String url, String year) {
        this.id = id;
        this.serialId = serialId;
        this.url = url;
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
        return id == season.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url, id, name, year);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("serialId", serialId)
                .add("url", url)
                .add("name", name)
                .add("year", year)
                .toString();
    }
}
