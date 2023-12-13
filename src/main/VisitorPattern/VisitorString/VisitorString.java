package main.VisitorPattern.VisitorString;

import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public interface VisitorString {
    String visitString(SongSelection songSelection);
    String visitString(PodcastSelection podcastSelection);
    String visitString(PlaylistSelection playlistSelection);
    String visitString(AlbumSelection albumSelection);
}
