package main.visitorpattern.visitorstring.stringclasses;

import main.commandhelper.Command;
import main.selectionclasses.playlists.AlbumSelection;
import main.selectionclasses.playlists.PlaylistSelection;
import main.selectionclasses.PodcastSelection;
import main.selectionclasses.SongSelection;
import main.utilityclasses.getmessageclasses.GetMessages12;
import main.visitorpattern.visitorstring.VisitorString;

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
        return GetMessages12.getShuffleMessage(crtItem, crtCommand);
    }

    @Override
    public String visitString(final AlbumSelection crtItem) {
        //  All conditions met. Switch to shuffle/unshuffle
        //  Set the message and update playlist
        return GetMessages12.getShuffleMessage(crtItem, crtCommand);
    }
}
