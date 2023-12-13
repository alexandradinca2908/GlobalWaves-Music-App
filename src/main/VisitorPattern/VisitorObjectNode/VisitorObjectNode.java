package main.VisitorPattern.VisitorObjectNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public interface VisitorObjectNode {
    ObjectNode visitObjectNode(SongSelection songSelection);
    ObjectNode visitObjectNode(PodcastSelection podcastSelection);
    ObjectNode visitObjectNode(PlaylistSelection playlistSelection);
    ObjectNode visitObjectNode(AlbumSelection albumSelection);
}
