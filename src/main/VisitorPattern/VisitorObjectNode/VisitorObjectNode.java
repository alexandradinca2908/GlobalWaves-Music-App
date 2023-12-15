package main.VisitorPattern.VisitorObjectNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;

public interface VisitorObjectNode {
    /**
     * This method can be implemented to operate on
     * a SongSelection visitor and return an Object Node
     * @param songSelection song
     * @return Output ObjectNode
     */
    ObjectNode visitObjectNode(SongSelection songSelection);

    /**
     * This method can be implemented to operate on
     * a PodcastSelection visitor and return an Object Node
     * @param podcastSelection podcast
     * @return Output ObjectNode
     */
    ObjectNode visitObjectNode(PodcastSelection podcastSelection);

    /**
     * This method can be implemented to operate on
     * a PLaylistSelection visitor and return an Object Node
     * @param playlistSelection playlist
     * @return Output ObjectNode
     */
    ObjectNode visitObjectNode(PlaylistSelection playlistSelection);

    /**
     * This method can be implemented to operate on
     * an AlbumSelection visitor and return an Object Node
     * @param albumSelection album
     * @return Output ObjectNode
     */
    ObjectNode visitObjectNode(AlbumSelection albumSelection);
}
