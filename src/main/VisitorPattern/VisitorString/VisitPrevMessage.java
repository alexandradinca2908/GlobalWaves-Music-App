package main.VisitorPattern.VisitorString;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import main.CommandHelper.Command;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public final class VisitPrevMessage implements VisitorString {
    private Command crtCommand;

    public VisitPrevMessage(final Command crtCommand) {
        this.crtCommand = crtCommand;
    }

    @Override
    public String visitString(final SongSelection crtItem) {
        crtItem.setRemainingTime(crtItem.getSong().getDuration());
        crtItem.setPaused(false);

        return "Returned to previous track successfully. The current track is "
                + crtItem.getSong().getName() + ".";
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
                duration += episode.getDuration();
                crtEp = episode;
                break;
            }
        }

        if (duration - crtItem.getRemainingTime() > 1) {
            crtItem.setRemainingTime(duration);
            crtItem.setStartTime(crtCommand.getTimestamp());
            crtItem.setPaused(false);

            message = "Returned to previous track successfully. The current track is "
                    + crtEp.getName() + ".";

        } else {
            //  Treating first episode exception
            if (crtItem.getPodcast().getEpisodes().indexOf(crtEp) == 0) {
                //  If we are at the first episode, restart the podcast
                crtItem.setRemainingTime(crtItem.getPodcast().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. The current track is "
                        + crtItem.getPodcast().getEpisodes().get(0).getName() + ".";

                //  Lastly we can go back to the previous episode
            } else {
                int index = crtItem.getPodcast().getEpisodes().indexOf(crtEp) - 1;
                crtItem.setRemainingTime(duration
                        + crtItem.getPodcast().getEpisodes().get(index).getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. The current track is "
                        + crtItem.getPodcast().getEpisodes().get(0).getName() + ".";
            }
        }

        return message;
    }

    @Override
    public String visitString(final PlaylistSelection crtItem) {
        String message = null;
        SongInput crtSong = null;

        //  Find the current song
        int duration = crtItem.getPlaylist().getDuration();

        for (SongInput song : crtItem.getPlaylist().getSongs()) {
            duration -= song.getDuration();

            if (duration < crtItem.getRemainingTime()) {
                duration += song.getDuration();
                crtSong = song;
                break;
            }
        }

        if (duration - crtItem.getRemainingTime() > 1) {
            crtItem.setRemainingTime(duration);
            crtItem.setStartTime(crtCommand.getTimestamp());
            crtItem.setPaused(false);

            message = "Returned to previous track successfully. The current track is "
                    + crtSong.getName() + ".";

        } else {
            //  Treating first song exception
            if (crtItem.getPlaylist().getSongs().indexOf(crtSong) == 0) {
                //  If we are at the first song, just restart the playlist
                crtItem.setRemainingTime(crtItem.getPlaylist().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. "
                        + "The current track is "
                        + crtItem.getPlaylist().getSongs().get(0).getName() + ".";

                //  Now we can go back to the previous song
            } else {
                int index = crtItem.getPlaylist().getSongs().indexOf(crtSong) - 1;
                crtItem.setRemainingTime(duration
                        + crtItem.getPlaylist().getSongs().get(index).getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. The current track is "
                        + crtItem.getPlaylist().getSongs().get(index).getName() + ".";
            }
        }

        return message;
    }

    @Override
    public String visitString(final AlbumSelection crtItem) {
        String message = null;
        SongInput crtSong = null;

        //  Find the current song
        int duration = crtItem.getAlbum().getDuration();

        for (SongInput song : crtItem.getAlbum().getSongs()) {
            duration -= song.getDuration();

            if (duration < crtItem.getRemainingTime()) {
                duration += song.getDuration();
                crtSong = song;
                break;
            }
        }

        if (duration - crtItem.getRemainingTime() > 1) {
            crtItem.setRemainingTime(duration);
            crtItem.setStartTime(crtCommand.getTimestamp());
            crtItem.setPaused(false);

            message = "Returned to previous track successfully. The current track is "
                    + crtSong.getName() + ".";

        } else {
            //  Treating first song exception
            if (crtItem.getAlbum().getSongs().indexOf(crtSong) == 0) {
                //  If we are at the first song, just restart the playlist
                crtItem.setRemainingTime(crtItem.getAlbum().getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. "
                        + "The current track is "
                        + crtItem.getAlbum().getSongs().get(0).getName() + ".";

                //  Now we can go back to the previous song
            } else {
                int index = crtItem.getAlbum().getSongs().indexOf(crtSong) - 1;
                crtItem.setRemainingTime(duration
                        + crtItem.getAlbum().getSongs().get(index).getDuration());
                crtItem.setStartTime(crtCommand.getTimestamp());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. The current track is "
                        + crtItem.getAlbum().getSongs().get(index).getName() + ".";
            }
        }

        return message;
    }
}
