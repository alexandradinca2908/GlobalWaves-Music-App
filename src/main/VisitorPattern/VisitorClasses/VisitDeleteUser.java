package main.VisitorPattern.VisitorClasses;

import main.CommandHelper.Command;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.VisitorPattern.Visitor;

public final class VisitDeleteUser implements Visitor {
    private Command crtCommand;

    public VisitDeleteUser(final Command crtCommand) {
        this.crtCommand = crtCommand;
    }

    @Override
    public String visit(final SongSelection crtItem) {
        if ((crtItem.getSong().getArtist().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }

    @Override
    public String visit(final PodcastSelection crtItem) {
        if ((crtItem.getPodcast().getOwner().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }

    @Override
    public String visit(final PlaylistSelection crtItem) {
        if ((crtItem.getPlaylist().getOwner().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }

    @Override
    public String visit(final AlbumSelection crtItem) {
        if ((crtItem.getAlbum().getOwner().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }
}
