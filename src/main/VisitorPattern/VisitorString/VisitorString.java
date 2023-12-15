package main.VisitorPattern.VisitorString;

import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public interface VisitorString {
    /**
     * This method can be implemented to operate on
     * a SongSelection visitor and return a string
     * @param songSelection song
     * @return Any string
     */
    String visitString(SongSelection songSelection);

    /**
     * This method can be implemented to operate on
     * a PodcastSelection visitor and return a string
     * @param podcastSelection podcast
     * @return Any string
     */
    String visitString(PodcastSelection podcastSelection);

    /**
     * This method can be implemented to operate on
     * a PlaylistSelection visitor and return a string
     * @param playlistSelection playlist
     * @return Any string
     */
    String visitString(PlaylistSelection playlistSelection);

    /**
     * This method can be implemented to operate on
     * an AlbumSelection visitor and return a string
     * @param albumSelection album
     * @return Any string
     */
    String visitString(AlbumSelection albumSelection);
}
