package main.VisitorPattern;

import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public interface Visitor {
    public String visit(SongSelection songSelection);
    public String visit(PodcastSelection podcastSelection);
    public String visit(PlaylistSelection playlistSelection);
}
