package main.VisitorPattern.VisitorString.Classes;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandHelper.Command;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.UtilityClasses.GetMessages;
import main.VisitorPattern.VisitorObjectNode.VisitorObjectNode;
import main.VisitorPattern.VisitorString.VisitorString;

public class VisitShuffle implements VisitorString {
    Command crtCommand;

    public VisitShuffle(Command crtCommand) {
        this.crtCommand = crtCommand;
    }

    @Override
    public String visitString(SongSelection crtItem) {
        return "The loaded source is not a playlist "
                +  "or an album.";
    }

    @Override
    public String visitString(PodcastSelection crtItem) {
        return "The loaded source is not a playlist "
                +  "or an album.";
    }

    @Override
    public String visitString(PlaylistSelection crtItem) {
        //  All conditions met. Switch to shuffle/unshuffle
        //  Set the message and update playlist
        return GetMessages.getShuffleMessage(crtItem, crtCommand);
    }

    @Override
    public String visitString(AlbumSelection crtItem) {
        //  All conditions met. Switch to shuffle/unshuffle
        //  Set the message and update playlist
        return GetMessages.getShuffleMessage(crtItem, crtCommand);
    }
}
