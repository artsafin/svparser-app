package in.artsaf.seriesapp.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class Serial implements Serializable {
    public long id;
    public String name;
    public String image;

    public Serial() { }

    public Serial(long id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Serial serial = (Serial) o;
        return id == serial.id &&
                Objects.equal(name, serial.name) &&
                Objects.equal(image, serial.image);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, image);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("image", image)
                .toString();
    }
}
