package in.artsaf.seriesapp.seasonvar;

public class PlaylistItem {
    public String file;
    public String comment;

    public PlaylistItem(String file, String comment) {
        this.file = file;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return comment.replace("<br>", "\n");
    }
}
