package main.visitorpattern.visitorobjectnode.objectnodeclasses;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import main.selectionclasses.playlists.AlbumSelection;
import main.selectionclasses.playlists.PlaylistSelection;
import main.selectionclasses.PodcastSelection;
import main.selectionclasses.SongSelection;
import main.visitorpattern.visitorobjectnode.VisitorObjectNode;

public final class VisitGetStats implements VisitorObjectNode {
    private ObjectNode stats;

    public VisitGetStats(final ObjectNode stats) {
        this.stats = stats;
    }
    @Override
    public ObjectNode visitObjectNode(final SongSelection reqItem) {
        SongInput songItem = reqItem.getSong();

        //  Check remaining time
        int remainingTime = reqItem.getRemainingTime();

        //  Set name
        if (remainingTime == 0) {
            stats.put("name", "");
        } else {
            stats.put("name", songItem.getName());
        }

        //  Set remaining time
        stats.put("remainedTime", remainingTime);

        //  Set repeat status
        stats.put("repeat", reqItem.getRepeat());

        //  Set shuffle
        stats.put("shuffle", reqItem.isShuffle());

        //  Set paused
        stats.put("paused", reqItem.isPaused());

        return stats;
    }

    @Override
    public ObjectNode visitObjectNode(final PodcastSelection reqItem) {
        //  Check remaining time
        int remainingTime = reqItem.getRemainingTime();

        if (remainingTime == 0) {
            //  Set name
            stats.put("name", "");

            //  Set remaining time
            stats.put("remainedTime", 0);
        } else {
            //  We find the current episode
            EpisodeInput crtEpisode = null;
            int duration = reqItem.getPodcast().getDuration();

            for (EpisodeInput episode : reqItem.getPodcast().getEpisodes()) {
                duration -= episode.getDuration();

                if (duration < remainingTime) {
                    crtEpisode = episode;
                    break;
                }
            }

            //  Set name
            stats.put("name", crtEpisode.getName());

            //  Set remaining time
            stats.put("remainedTime", remainingTime - duration);
        }

        //  Set repeat status
        stats.put("repeat", reqItem.getRepeat());

        //  Set shuffle
        stats.put("shuffle", reqItem.isShuffle());

        //  Set paused
        stats.put("paused", reqItem.isPaused());

        return stats;
    }

    @Override
    public ObjectNode visitObjectNode(final PlaylistSelection reqItem) {
        //  Check remaining time
        int remainingTime = reqItem.getRemainingTime();

        if (remainingTime == 0) {
            //  Set name
            stats.put("name", "");

            //  Set remaining time
            stats.put("remainedTime", 0);
        } else {
            //  We find the current song
            SongInput crtSong = null;

            int duration = reqItem.getPlaylist().getDuration();

            if (!reqItem.isShuffle()) {
                for (SongInput song : reqItem.getPlaylist().getSongs()) {
                    duration -= song.getDuration();

                    if (duration < remainingTime) {
                        crtSong = song;
                        break;
                    }
                }
            } else {
                for (SongInput song : reqItem.getShuffledPlaylist()) {
                    duration -= song.getDuration();

                    if (duration < remainingTime) {
                        crtSong = song;
                        break;
                    }
                }
            }

            //  Set name
            stats.put("name", crtSong.getName());

            //  Set remaining time
            stats.put("remainedTime", remainingTime - duration);
        }

        //  Set repeat status
        stats.put("repeat", reqItem.getRepeat());

        //  Set shuffle
        stats.put("shuffle", reqItem.isShuffle());

        //  Set paused
        stats.put("paused", reqItem.isPaused());

        return stats;
    }

    @Override
    public ObjectNode visitObjectNode(final AlbumSelection reqItem) {
        //  Check remaining time
        int remainingTime = reqItem.getRemainingTime();

        if (remainingTime == 0) {
            //  Set name
            stats.put("name", "");

            //  Set remaining time
            stats.put("remainedTime", 0);
        } else {
            //  We find the current song
            SongInput crtSong = null;

            int duration = reqItem.getAlbum().getDuration();

            if (!reqItem.isShuffle()) {
                for (SongInput song : reqItem.getAlbum().getSongs()) {
                    duration -= song.getDuration();

                    if (duration < remainingTime) {
                        crtSong = song;
                        break;
                    }
                }
            } else {
                for (SongInput song : reqItem.getShuffledAlbum()) {
                    duration -= song.getDuration();

                    if (duration < remainingTime) {
                        crtSong = song;
                        break;
                    }
                }
            }

            //  Set name
            stats.put("name", crtSong.getName());

            //  Set remaining time
            stats.put("remainedTime", remainingTime - duration);
        }

        //  Set repeat status
        stats.put("repeat", reqItem.getRepeat());

        //  Set shuffle
        stats.put("shuffle", reqItem.isShuffle());

        //  Set paused
        stats.put("paused", reqItem.isPaused());

        return stats;
    }
}
