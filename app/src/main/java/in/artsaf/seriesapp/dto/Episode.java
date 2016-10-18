package in.artsaf.seriesapp.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Episode {
    public String comment;
    public String file;

    public Episode(String comment, String file) {
        this.comment = comment;
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return Objects.equal(comment, episode.comment) &&
                Objects.equal(file, episode.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comment, file);
    }

    @Override
    public String toString() {
        return comment.replace("<br>", "\n");
    }
}
