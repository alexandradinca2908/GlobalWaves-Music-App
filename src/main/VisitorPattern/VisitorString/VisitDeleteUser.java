package main.VisitorPattern.VisitorString;

import main.CommandHelper.Command;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public final class VisitDeleteUser implements VisitorString {
    private Command crtCommand;

    public VisitDeleteUser(final Command crtCommand) {
        this.crtCommand = crtCommand;
    }

    @Override
    public String visitString(final SongSelection crtItem) {
        if ((crtItem.getSong().getArtist().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }

    @Override
    public String visitString(final PodcastSelection crtItem) {
        if ((crtItem.getPodcast().getOwner().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }

    @Override
    public String visitString(final PlaylistSelection crtItem) {
        if ((crtItem.getPlaylist().getOwner().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }

    @Override
    public String visitString(final AlbumSelection crtItem) {
        if ((crtItem.getAlbum().getOwner().equals(crtCommand.getUsername()))) {
            return "true";
        }
        return "false";
    }
}
