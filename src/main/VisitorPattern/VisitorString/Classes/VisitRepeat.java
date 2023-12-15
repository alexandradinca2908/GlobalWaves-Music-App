package main.VisitorPattern.VisitorString.Classes;

import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.VisitorPattern.VisitorString.VisitorString;

public final class VisitRepeat implements VisitorString {
    @Override
    public String visitString(final SongSelection crtItem) {
        String message = null;

        switch (crtItem.getRepeat()) {
            case "No Repeat" -> {
                crtItem.setRepeat("Repeat Once");
                message = "Repeat mode changed to repeat once.";
            }
            case "Repeat Once" -> {
                crtItem.setRepeat("Repeat Infinite");
                message = "Repeat mode changed to repeat infinite.";
            }
            case "Repeat Infinite" -> {
                crtItem.setRepeat("No Repeat");
                message = "Repeat mode changed to no repeat.";
            }
            default -> {
            }
        }

        return message;
    }

    @Override
    public String visitString(final PodcastSelection crtItem) {
        String message = null;

        switch (crtItem.getRepeat()) {
            case "No Repeat" -> {
                crtItem.setRepeat("Repeat Once");
                message = "Repeat mode changed to repeat once.";
            }
            case "Repeat Once" -> {
                crtItem.setRepeat("Repeat Infinite");
                message = "Repeat mode changed to repeat infinite.";
            }
            case "Repeat Infinite" -> {
                crtItem.setRepeat("No Repeat");
                message = "Repeat mode changed to no repeat.";
            }
            default -> {
            }
        }

        return message;
    }

    @Override
    public String visitString(final PlaylistSelection crtItem) {
        String message = null;

        switch (crtItem.getRepeat()) {
            case "No Repeat" -> {
                crtItem.setRepeat("Repeat All");
                message = "Repeat mode changed to repeat all.";
            }
            case "Repeat All" -> {
                crtItem.setRepeat("Repeat Current Song");
                message = "Repeat mode changed to repeat current song.";

                //  Setting intervals for the song loop
                PlaylistSelection.setIntervals(crtItem);
            }
            case "Repeat Current Song" -> {
                crtItem.setRepeat("No Repeat");
                message = "Repeat mode changed to no repeat.";

                //  Reset intervals
                crtItem.setStartTimestamp(-1);
                crtItem.setStopTimestamp(-1);
            }
            default -> {
            }
        }

        return message;
    }

    @Override
    public String visitString(final AlbumSelection crtItem) {
        String message = null;

        switch (crtItem.getRepeat()) {
            case "No Repeat" -> {
                crtItem.setRepeat("Repeat All");
                message = "Repeat mode changed to repeat all.";
            }
            case "Repeat All" -> {
                crtItem.setRepeat("Repeat Current Song");
                message = "Repeat mode changed to repeat current song.";

                //  Setting intervals for the song loop
                AlbumSelection.setIntervals(crtItem);
            }
            case "Repeat Current Song" -> {
                crtItem.setRepeat("No Repeat");
                message = "Repeat mode changed to no repeat.";

                //  Reset intervals
                crtItem.setStartTimestamp(-1);
                crtItem.setStopTimestamp(-1);
            }
            default -> {
            }
        }

        return message;
    }
}
