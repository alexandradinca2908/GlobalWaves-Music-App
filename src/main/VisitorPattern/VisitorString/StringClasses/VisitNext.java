package main.VisitorPattern.VisitorString.StringClasses;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import main.CommandHelper.Command;
import main.SelectionClasses.SongSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.VisitorPattern.VisitorString.VisitorString;

import java.util.ArrayList;

public final class VisitNext implements VisitorString {
    private ArrayList<ItemSelection> player;
    private ArrayList<PodcastSelection> podcasts;
    private Command crtCommand;

    public VisitNext(final ArrayList<ItemSelection> player,
                     final ArrayList<PodcastSelection> podcasts,
                     final Command crtCommand) {
        this.player = player;
        this.podcasts = podcasts;
        this.crtCommand = crtCommand;
    }

    @Override
    public String visitString(final SongSelection crtItem) {
        crtItem.setRemainingTime(0);
        crtItem.setPaused(true);

        player.remove(crtItem);

        return "Please load a source before skipping to the next track.";
    }

    @Override
    public String visitString(final PodcastSelection crtItem) {
        String message = null;

        EpisodeInput crtEp = null;

        //  Find the current episode
        int duration = crtItem.getPodcast().getDuration();

        for (EpisodeInput episode : crtItem.getPodcast().getEpisodes()) {
            duration -= episode.getDuration();

            if (duration < crtItem.getRemainingTime()) {
                crtEp = episode;
                break;
            }
        }

        if (duration > 0) {
            crtItem.setRemainingTime(duration);
            crtItem.setPaused(false);
            int index = crtItem.getPodcast().getEpisodes().indexOf(crtEp);

            message = "Skipped to next track successfully. The current track is "
                    + crtItem.getPodcast().getEpisodes().get(index + 1).getName() + ".";

        } else if (duration == 0) {
            //  Check repeat status to take action
            if (crtItem.getRepeat().equals("No Repeat")) {
                //  Stop podcast
                crtItem.setRemainingTime(0);
                crtItem.setPaused(true);
                player.remove(crtItem);
                podcasts.remove(crtItem);

                message = "Please load a source before skipping to the next track.";

            } else if (crtItem.getRepeat().equals("Repeat Once")) {
                //  Restart podcast
                crtItem.setRemainingTime(crtItem.getPodcast().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);
                crtItem.setRepeat("No Repeat");

                message = "Skipped to next track successfully. The current track is "
                        + crtItem.getPodcast().getEpisodes().get(0).getName() + ".";

            } else if (crtItem.getRepeat().equals("Repeat Infinite")) {
                //  Restart podcast but keep repeat status
                crtItem.setRemainingTime(crtItem.getPodcast().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Skipped to next track successfully. The current track is "
                        + crtItem.getPodcast().getEpisodes().get(0).getName() + ".";
            }
        }

        return message;
    }

    @Override
    public String visitString(final PlaylistSelection crtItem) {
        String message = null;

        SongInput crtSong = null;
        ArrayList<SongInput> songOrder = null;

        //  Find the current song
        int duration = crtItem.getPlaylist().getDuration();
        int prevDuration = duration;

        if (!crtItem.isShuffle()) {
            songOrder = crtItem.getPlaylist().getSongs();
        } else {
            songOrder = crtItem.getShuffledPlaylist();
        }

        for (SongInput song : songOrder) {
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

                int index = songOrder.indexOf(crtSong);

                message = "Skipped to next track successfully. The current track is "
                        + songOrder.get(index + 1).getName() + ".";
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
                        + songOrder.get(0).getName() + ".";

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

    @Override
    public String visitString(final AlbumSelection crtItem) {
        String message = null;

        SongInput crtSong = null;
        ArrayList<SongInput> songOrder = null;

        //  Find the current song
        int duration = crtItem.getAlbum().getDuration();
        int prevDuration = duration;

        if (!crtItem.isShuffle()) {
            songOrder = crtItem.getAlbum().getSongs();
        } else {
            songOrder = crtItem.getShuffledAlbum();
        }

        for (SongInput song : songOrder) {
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

                int index = songOrder.indexOf(crtSong);

                message = "Skipped to next track successfully. The current track is "
                        + songOrder.get(index + 1).getName() + ".";
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
                crtItem.setRemainingTime(crtItem.getAlbum().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Skipped to next track successfully. The current track is "
                        + songOrder.get(0).getName() + ".";

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
