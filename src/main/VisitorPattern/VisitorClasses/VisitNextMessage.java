package main.VisitorPattern.VisitorClasses;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import main.CommandHelper.Command;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.VisitorPattern.Visitor;

import java.util.ArrayList;

public final class VisitNextMessage implements Visitor {
    private ArrayList<ItemSelection> player;
    private ArrayList<PodcastSelection> podcasts;
    private Command crtCommand;

    public VisitNextMessage(final ArrayList<ItemSelection> player,
                            final ArrayList<PodcastSelection> podcasts,
                            final Command crtCommand) {
        this.player = player;
        this.podcasts = podcasts;
        this.crtCommand = crtCommand;
    }

    @Override
    public String visit(final SongSelection crtItem) {
        crtItem.setRemainingTime(0);
        crtItem.setPaused(true);

        player.remove(crtItem);

        return "Please load a source before skipping to the next track.";
    }

    @Override
    public String visit(final PodcastSelection crtItem) {
        String message = null;

        PodcastSelection copyItem = (PodcastSelection) crtItem;
        EpisodeInput crtEp = null;

        //  Find the current episode
        int duration = copyItem.getPodcast().getDuration();

        for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
            duration -= episode.getDuration();

            if (duration < copyItem.getRemainingTime()) {
                crtEp = episode;
                break;
            }
        }

        if (duration > 0) {
            copyItem.setRemainingTime(duration);
            copyItem.setPaused(false);
            int index = copyItem.getPodcast().getEpisodes().indexOf(crtEp);

            message = "Skipped to next track successfully. The current track is "
                    + copyItem.getPodcast().getEpisodes().get(index + 1).getName() + ".";

        } else if (duration == 0) {
            //  Check repeat status to take action
            if (copyItem.getRepeat().equals("No Repeat")) {
                //  Stop podcast
                copyItem.setRemainingTime(0);
                copyItem.setPaused(true);
                player.remove(copyItem);
                podcasts.remove(copyItem);

                message = "Please load a source before skipping to the next track.";

            } else if (copyItem.getRepeat().equals("Repeat Once")) {
                //  Restart podcast
                copyItem.setRemainingTime(copyItem.getPodcast().getDuration());
                copyItem.setStartTime(crtCommand.getTimestamp());
                copyItem.setPaused(false);
                copyItem.setRepeat("No Repeat");

                message = "Skipped to next track successfully. The current track is "
                        + copyItem.getPodcast().getEpisodes().get(0).getName() + ".";

            } else if (copyItem.getRepeat().equals("Repeat Infinite")) {
                //  Restart podcast but keep repeat status
                copyItem.setRemainingTime(copyItem.getPodcast().getDuration());
                copyItem.setStartTime(crtCommand.getTimestamp());
                copyItem.setPaused(false);

                message = "Skipped to next track successfully. The current track is "
                        + copyItem.getPodcast().getEpisodes().get(0).getName() + ".";
            }
        }

        return message;
    }

    @Override
    public String visit(final PlaylistSelection crtItem) {
        String message = null;

        SongInput crtSong = null;

        //  Find the current song
        int duration = crtItem.getPlaylist().getDuration();
        int prevDuration = duration;

        for (SongInput song : crtItem.getPlaylist().getSongs()) {
            prevDuration = duration;
            duration -= song.getDuration();

            if (duration < crtItem.getRemainingTime()) {
                crtSong = song;
                break;
            }
        }

        if (duration > 0) {
            if (crtItem.getRepeat().equals("Repeat Current Song")) {
                //  Restart song
                crtItem.setRemainingTime(prevDuration);
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Skipped to next track successfully. The current track is "
                        + crtSong.getName() + ".";
            } else {
                //  Next song
                crtItem.setRemainingTime(duration);
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                int index = crtItem.getPlaylist().getSongs().indexOf(crtSong);

                message = "Skipped to next track successfully. The current track is "
                        + crtItem.getPlaylist().getSongs().get(index + 1).getName() + ".";
            }
        } else if (duration == 0) {
            //  Check repeat status to take action
            if (crtItem.getRepeat().equals("No Repeat")) {
                //  Stop playlist
                crtItem.setRemainingTime(0);
                crtItem.setPaused(true);
                player.remove(crtItem);

                message = "Please load a source before skipping to the next track.";

            } else if (crtItem.getRepeat().equals("Repeat All")) {
                //  Restart playlist
                crtItem.setRemainingTime(crtItem.getPlaylist().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Skipped to next track successfully. The current track is "
                        + crtItem.getPlaylist().getSongs().get(0).getName() + ".";

            } else if (crtItem.getRepeat().equals("Repeat Current Song")) {
                //  Restart song
                crtItem.setRemainingTime(prevDuration);
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Skipped to next track successfully. The current track is "
                        + crtSong.getName() + ".";
            }
        }

        return message;
    }
}
