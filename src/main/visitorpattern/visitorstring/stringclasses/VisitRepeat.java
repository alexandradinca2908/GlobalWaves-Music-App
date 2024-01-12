package main.visitorpattern.visitorstring.stringclasses;

import main.selectionclasses.playlists.AlbumSelection;
import main.selectionclasses.playlists.PlaylistSelection;
import main.selectionclasses.PodcastSelection;
import main.selectionclasses.SongSelection;
import main.visitorpattern.visitorstring.VisitorString;

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
                if (!crtItem.isShuffle()) {
                    PlaylistSelection.setIntervals(crtItem);
                } else {
                    PlaylistSelection.setIntervalsShuffle(crtItem);
                }
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
                if (!crtItem.isShuffle()) {
                    AlbumSelection.setIntervals(crtItem);
                } else {
                    AlbumSelection.setIntervalsShuffle(crtItem);
                }
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
