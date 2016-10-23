package in.artsaf.seriesapp.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

public class Episode {
    public long _id;
    public String comment;
    public String file;
    public Playlist playlist;

    public Episode(long id, String comment, String file) {
        this._id = id;
        this.comment = comment;
        this.file = file;
    }

    public boolean isSingle() {
        return playlist == null;
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
        return MoreObjects.toStringHelper(this)
                .add("_id", _id)
                .add("comment", comment)
                .add("file", file)
                .add("playlist", playlist)
                .toString();
    }

    public Episode normalize(long id) {
        return new Episode(id, comment.replace("<br>", "\n"), file);
    }
}
