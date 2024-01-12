package main.visitorpattern.visitorstring.stringclasses;

import fileio.input.SongInput;
import main.commandhelper.Command;
import main.selectionclasses.playlists.AlbumSelection;
import main.selectionclasses.playlists.PlaylistSelection;
import main.selectionclasses.PodcastSelection;
import main.selectionclasses.SongSelection;
import main.visitorpattern.visitorstring.VisitorString;

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

        //  We find the current song
        SongInput crtSong = null;

        int duration = crtItem.getPlaylist().getDuration();
        int remainingTime = crtItem.getRemainingTime();

        if (!crtItem.isShuffle()) {
            for (SongInput song : crtItem.getPlaylist().getSongs()) {
                duration -= song.getDuration();

                if (duration < remainingTime) {
                    crtSong = song;
                    break;
                }
            }
        } else {
            for (SongInput song : crtItem.getShuffledPlaylist()) {
                duration -= song.getDuration();

                if (duration < remainingTime) {
                    crtSong = song;
                    break;
                }
            }
        }
        //  Check if the current song belongs to the user
        if (crtSong.getArtist().equals(crtCommand.getUsername())) {
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
