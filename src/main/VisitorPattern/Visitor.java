package main.VisitorPattern;

import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public interface Visitor {
    String visit(SongSelection songSelection);
    String visit(PodcastSelection podcastSelection);
    String visit(PlaylistSelection playlistSelection);
}
