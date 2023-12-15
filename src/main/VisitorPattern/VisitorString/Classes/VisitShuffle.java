package main.VisitorPattern.VisitorString.Classes;

import main.CommandHelper.Command;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.UtilityClasses.GetMessages;
import main.VisitorPattern.VisitorString.VisitorString;

public final class VisitShuffle implements VisitorString {
    private Command crtCommand;

    public VisitShuffle(final Command crtCommand) {
        this.crtCommand = crtCommand;
    }

    @Override
    public String visitString(final SongSelection crtItem) {
        return "The loaded source is not a playlist "
                +  "or an album.";
    }

    @Override
    public String visitString(final PodcastSelection crtItem) {
        return "The loaded source is not a playlist "
                +  "or an album.";
    }

    @Override
    public String visitString(final PlaylistSelection crtItem) {
        //  All conditions met. Switch to shuffle/unshuffle
        //  Set the message and update playlist
        return GetMessages.getShuffleMessage(crtItem, crtCommand);
    }

    @Override
    public String visitString(final AlbumSelection crtItem) {
        //  All conditions met. Switch to shuffle/unshuffle
        //  Set the message and update playlist
        return GetMessages.getShuffleMessage(crtItem, crtCommand);
    }
}
