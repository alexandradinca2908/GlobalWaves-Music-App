package main.VisitorPattern;

import main.CommandHelper.Command;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

import java.util.ArrayList;

public interface Visitor {
    public String visit(SongSelection songSelection);
    public String visit(PodcastSelection podcastSelection);
    public String visit(PlaylistSelection playlistSelection);
}
