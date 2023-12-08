package main.VisitorPattern;

import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public class VisitNextMessage implements Visitor{
    @Override
    public String visit(SongSelection songSelection) {
        return null;
    }

    @Override
    public String visit(PodcastSelection podcastSelection) {
        return null;
    }

    @Override
    public String visit(PlaylistSelection playlistSelection) {
        return null;
    }
}
