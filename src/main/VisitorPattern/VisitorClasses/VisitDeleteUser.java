package main.VisitorPattern.VisitorClasses;

import main.CommandHelper.Command;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.VisitorPattern.Visitor;

import java.util.ArrayList;

public class VisitDeleteUser implements Visitor {
    private ArrayList<ItemSelection> player;
    private ArrayList<PodcastSelection> podcasts;
    private Command crtCommand;

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

    @Override
    public String visit(AlbumSelection albumSelection) {
        return null;
    }
}
