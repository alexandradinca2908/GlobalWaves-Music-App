# Proiect GlobalWaves - Etapa 1 - CLASS DESCRIPTION
## **Command**
This class represents any command that the user may input. When reading into an instance of this class 
from a JSON file, the unnecessary fields will just be set to null/0.

## **Constants**
This utility class contains all constants that are required for the program.

## **Filters**
This class represents all the possible filters that a search command can receive. It is in an aggregation 
relationship with _Command_ class.

##  **GetMessages**
This utility class contains all the static methods required to make a change into the database and then return
the appropriate message based on how the operation went.

    getShuffleMessage: this method treats the 2 cases (shuffle and unshuffle)

        shuffle: we keep the unshuffled order, pinpoint the current song,
        shuffle the songs, set the song and the exact timestamp we were 
        at before and finally set the shuffle status to true

        unshuffle: pinpoint the current song, restore original order and
        finally set the song and the exact timestamp we were at before
        the operation

    getSelectMessage: this method checks how the select operation went
        based on the contents of the helper variables and sets the 
        message accordingly

    getAddRemoveMessage: after checking if the player has a song loaded
        and if the given playlist is available, this method either adds
        or removes the current song from the playlist by simply iterating
        through the song arrays in the selected playlist instance

    getLikeMessage: similarly to the method above, this function adds or
        removes the like a user has given to a song by iterating through
        the liked songs of the user and seeing whether it has been liked
        already or not

    getForwardMessage: this method skips 90 seconds from the current
        playing podcast; after making sure that the given player has
        a podcast loaded, it skips these 90 seconds and then checks
        wheter or not this action moves the user to the beginning of 
        the next song. Moreover, if this action ends the podcast, it 
        may have to be restarted if the repeat state tells us so.

    getBackwardMessage: this method goes back 90 seconds from the
        current playing podcast; after making sure that the given
        player has a podcast loaded, it goes back these 90 seconds
        and then checks wheter or not this action moves the user to
        the beginning of the previous song.

    getNextMessage: this method skips the player to the next track;
        if the player has a song, it is simply ended, else we either
        skip to the next track, start from the beginning if the 
        repeat state allows us so or end the playlist/podcast

    getPrevMessage: this method skips the player to the previous
        track; if the current track has been playing for less than
        a second, we go to the previous song, else we just restart
        the current track

    getFollowMessage: this method allows a user to follow or
        unfollow a playlist by simply checking the list of
        followed playlist that a user has on their profile; if
        we can find the playlist, it means we have to remove it;
        if we dont, we must add it

    getSwitchVisibilityMessage: this method alternates between
        switching the boolean variable of visibility from true
        to false and vice versa

## **ItemSelection**
This class is the general representation of an item that can be selected and loaded into the player. It 
is subclassed in the three types of possible selections: _SongSelection, PlaylistSelection and PodcastSelection._
    
    getStats: this method analyses and retrieves the stats of an Item Selection,
        wrapped in an ObjectNode updateRemainingTime: updates the time based on 
        the selection type

## **SongSelection**
This class is the specific extension of ItemSelection that represents a selected song.
    
    updateRemainingTime: stops a song if the time left is 0 or restarts it is 
        the repeat status allows so

## **PlaylistSelection**
This class is the specific extension of ItemSelection that represents a selected playlist.
    
    updateRemainingTime: stops the playlist if the time left is 0 or restarts 
    the playlist/song based on the repeat status

## **PodcastSelection**
This class is the specific extension of ItemSelection that represents a selected podcast.

    updateRemainingTime: stops the playlist if the time left is 0 or restarts it is 
        the repeat status allows so

## **Playlist**
This class represents any playlist. It keeps track of the name, songs, followers, owner 
and even the original order of its songs before shuffling.
    
    getDuration: sums the durations of all the songs in the playlist

## **SearchSelect**
This utility class contains all the static methods required for the search and select
commands
    
    searchForSongs: based on the given filters, this method searches through
        the library for all the songs that match

    searchForPlaylists: based on the given filters, this method searches through
        the library for all the playlists that match
    
    searchForPodcasts: based on the given filters, this method searches through
        the library for all the podcasts that match

    getSongSelection: having the result from a search, it retrieves the song
        that matches the given ID
    
    getPlaylistSelection: having the result from a search, it retrieves the 
        playlist that matches the given ID
        
    getPodcastSelection: having the result from a search, it retrieves the 
        podcast that matches the given ID

    storeResultForSelect: stores in an array the name and type of the selection

## **SongLikes**
This class represents a song from the library paired with all its likes from the 
app users

## **UserPlaylist**
This class represents a user and all their playlists: followed playlists and liked
songs

## **Main**
In this class everything is bound together: after reading the database and the
commands from the JSON files, the _action_ method calls a big **SWITCH** statement
that executes all commands, one by one.
    
    updatePlayer: this method updates the times of all loaded and unpaused 
        players 
            