package main.visitorpattern.visitorobjectnode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.selectionclasses.playlists.AlbumSelection;
import main.selectionclasses.playlists.PlaylistSelection;
import main.selectionclasses.PodcastSelection;
import main.selectionclasses.SongSelection;

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
