# Proiect GlobalWaves - Etapa 2 - CLASS DESCRIPTION

## _Package : commandhelper_

## **Command**
This class represents any command that the user may input. When reading into an instance of this class 
from a JSON file, the unnecessary fields will just be set to null/0.

## **Filters**
This class represents all the possible filters that a search command can receive. It is in an aggregation
relationship with _Command_ class.

## **Search**
This class represents the format of a search. It contains the user that searched, the latest result of the
search and the steps that need to be completed before loading/following (step1 - search, step2 - select)

## _Package : creatorclasses_

## **Event**
This class represents an event that an artist may add. It has the 3 main attributes of an event: name,
description and date.

## **Merch**
This class represents any merch that an artist can add, having the specified attributes: name, description
and price.

## **Management**
The management of an artist represents the collection of all events and merches they can have.

## **Announcement**
This class represents an announcement of a host; it is distinguished by name and description.

## **HostInfo**
HostInfo is the collection of all announcements made by a certain host.

## **CreatorChannel**
This class represents a channel of an artist/host, having an identifier (creator name) and a list of subscribers.

## **NotificationBar**
This class represents the collection of notifications each user can get if they subscribe to channels.

## _Package : likeclasses_

## **ArtistLikes**
This class is the representation of an artist, linked with their total likes. In this way, when we need to
find the total number of likes an artist has, we can query an individual class. Since an artist is of
type UserInput, adding a field of likes for any user would have been redundant.

## **SongLikes**
This class represents a song from the library paired with all its likes from the app users

## _Package : monetization_

## **ArtistRevenue**
This class represents the end of program revenue template for each creator that may have generated revenue. It includes
a builder class.

## **PremiumUser**
This class links together a premium user and the songs they listened to. Used for revenue analysis.

## _Package : pagingclasses_

## **Page**
This class represents an individual page from the entire page system. Each user has their own page and,
based on what page they select, the user playlists change from the owner's playlists to the creator they
selected; when printing information about this page, the songs and playlists are right where
they are needed.

## _Package : playlistclasses_

## **Album**
This class represents an album. It is the extension of a playlist (see below), but has a few extra attributes.

## **Playlist**
This class represents any playlist. It keeps track of the name, songs, followers, owner and even the shuffled
order of its songs.

## **UserData**
This class represents a user and all their data: followed playlists, liked songs, bought merch

## _Package : selectionclasses_

## **ItemSelection**
This class is the general representation of an item that can be selected and loaded into the player. It
is subclassed in the four types of possible selections: _SongSelection, PlaylistSelection, PodcastSelection
and AlbumSelection._

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

    setIntervals(Shuffle) : updates the intervals based on the current time stamp
    and shuffle status

## **AlbumSelection**
This class is the specific extension of ItemSelection that represents a selected album.

    updateRemainingTime: stops the album if the time left is 0 or restarts 
    the album/song based on the repeat status

    setIntervals(Shuffle) : updates the intervals based on the current time stamp
    and shuffle status

## **PodcastSelection**
This class is the specific extension of ItemSelection that represents a selected podcast.

    updateRemainingTime: stops the podcast if the time left is 0 or restarts it is 
        the repeat status allows so

    getDuration: sums the durations of all the songs in the playlist

## _Package : utilityclasses_

## **Constants**
This utility class contains all constants that are required for the program.

## **DoCommands12/3**
These utility classes contain static methods that execute certain commands; most of the time, these functions either
don't require a final message or the process of acquiring the message was short enough to fit into the doCommand
method

    doSearch: this method clears any previous searches then stores up to 5
        searches based on the given filters; it also prepares the data to be
        selected

    doSelect: this method uses the given search to either select something playable
        and prepare it for loading or it changes the user's page to the selected
        creator's page

    doLoad: this method adds the given select to the player if all conditions are
        met and sets the timing variables to keep track of the media throughout the
        program

    doStatus: this method retrieves the current status of the player by using the
        ItemSelection method getStats()

    doPlayPause: this method switches the pause status of the user's item currently
        in the player

    doCreatePlaylist: this method creates a new playlist if all the conditions are met

    doShowPlaylists: this method displays all the playlists of a certain user

    doShowPreferredSongs : this method displays all liked songs of a certain user

    doRepeat: this method switches between repeat statuses, depending on the item
        that is currently being played by the user

    doShuffle: this method switched between shuffling statuses

    doFollow: this method follows the given select if all conditions are met

    doGetTop5Songs: this method displays most liked 5 songs from the database

    doGetTop5Playlists: this method displays most followed 5 playlists from the 
        database

    doGetOnlineUsers: this method displays all online users
    
    doShowAlbums: this method displays all albums of the chosen artist

    doPrintCurrentPage: this method prints data about the current page depending
        on what page the user is currently watching
    
    doGetAllUsers: this method displays all users in this order: users, artists,
        hosts

    doShowPodcasts: this method displays all podcasts of the chosen host

    doGetTop5Albums: this method displays most liked 5 albums from the database

    doGetTop5Artists: this method displays most liked 5 artists from the 
        database

    doGetNotification: this method displays all notifications from followed channels

    doSeeMerch: this method displays all the user's purchased merch
    
## **DoCommandsMessage12/3**
This utility class contains static wrapper methods that just call the associated getMessage function and return the
output ObjectNode

## **GetMessages12/3**
These utility classes contain all the static methods required to make a change into the database and then return
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

    getSwitchConnectionMessage : this method switches the user
        status from online/offline to offline/online and makes
        sure the timestamps don't update when the user is
        offline

    getAddUserMessage: this method adds a new user in the database
        if the username is new

    getAddAlbumMessage: this method adds an album and all of its
        songs in the database if the user exists, is an artist and
        the album itself meets all registration criteria

    getAddEventMessage: this method adds a new event in the user's
        profile (management) if it meets all the conditions

    getAddMerchMessage: this method adds new merch in the user's
        profile (management) if it meets all the conditions

    getDeleteUserMessage: this method carefully removes a user and
        all of its dependencies(if they are not being used) from every
        section of the database; it also makes sure to remove any
        connection between the user and the rest of the users

    getAddPodcastMessage: this method adds a podcast and all of its
        episodes in the database if the user exists, is a host and
        the podcast itself meets all registration criteria

    getAddAnnouncementMessage: this method adds a new announcement in
        the user's profile (hostinfo) if it meets all the conditions

    getRemoveAnnouncementMessage: this method safely deletes an announcement
        from the HostInfo

    getRemoveAlbumMessage: this method safely deletes an album from the
        database if the user has removal rights and the album is not being
        played in any way

    getChangePageMessage: this method changes the user's page by switching
        between Home and LikedContent

    getRemovePodcastMessage: this method safely deletes a podcast from the
        database if the user has removal rights and the podcast is not being
        played

    getRemoveEventMessage: this method safely deletes an event from
        Management

    getBuyPremiumMessage: this method helps a user buy a premium subscription
        and checks for exceptions

    getCancelPremiumMessage: this method helps a user cancel a premium subscription
        and checks for exceptions

    getSubscribeMessage: this method allows the users to subscribe/unsubscribe
        to any channel

    getBuyMerchMessage: this method adds a piece of merch to a user's data and
        keeps track of artist revenue

## **SearchSelect**
This utility class contains all the static methods required for the search and select
commands
    
    searchForSongs: based on the given filters, this method searches through
        the library for all the songs that match

    searchForPlaylists: based on the given filters, this method searches through
        the database for all the playlists that match
    
    searchForPodcasts: based on the given filters, this method searches through
        the library for all the podcasts that match

    searchForAlbums: based on the given filters, this method searches through
        the database for all the albums that match

    searchForCreators: based on the given filters, this method searches through
        the library for all the creators that match

    setCreatorSearchResults: this method polishes the final search result of
        the creators

    getSongSelection: having the result from a search, it retrieves the song
        that matches the given ID
    
    getPlaylistSelection: having the result from a search, it retrieves the 
        playlist that matches the given ID
        
    getPodcastSelection: having the result from a search, it retrieves the 
        podcast that matches the given ID

    getAlbumSelection: having the result from a search, it retrieves the 
        album that matches the given ID

    storeResultForSelect: stores in an array the name and type of the selection

## _**Package : visitorpattern**_
This package contains 2 sets of Visitor Patterns, one that returns Strings and one that returns ObjectNodes.
The visitables are always ItemSelection objects, while the visitors are methods that implement the logic of
the **getStatus, deleteUser, next, prev, repeat and shuffle** commands and finally return a certain output.
The main advantage of the Visitor Pattern in my application is reducing the usage of _instanceof_, code
cleanliness and better structure overall. Therefore, in order to check the logic behind those commands, you
can find them in their respective Visitor class.

## _**Package : wrappeddatabase**_
This package contains a singleton database that stores both creator and user wrapped, general statistics that are
inherited by more concrete classes and a factory pattern for database expansion.

## **GeneralStatistics**
This class is the general template of how a user's statistics will look like. It is inherited by ArtistStatistics,
HostStatistics and UserStatistics

## **Statistics**
Here is the Database. It is mainly made of 3 ArrayLists: one for artists, one for hosts and one for users. This class also
contains the function that formats the data into an appropriate output when a user wants to generate their wrapped.

## **StatsFactory**
This Factory Class adds a new user to the wrapped database, depending on their type.

## **Main**
In this class everything is bound together: after reading the database and the
commands from the JSON files, the _action_ method calls a big **SWITCH** statement
that executes all commands, one by one.
    
    updatePlayer: this method updates the times of all loaded and unpaused 
        players
    endProgram: this method calculates and displays generated revenue and sorts creators
        by how much money they made on the platform
            
## **USED DESIGN PATTERNS**

## **1.Visitor**
Visitor is located separately in its own package. This design pattern was necessary for a cleaner, more concise
and more POO approach when handling ItemSelection objects in the player. Instead of using _instanceof_ repeteadly on
certain commands that treat Selections differently, I implemented a visitor class for each respective method. Moreover,
this Visitor is separated into Visitor that returns ObjectNode items or String items.
_Note: due to distinctive interfaces, I had to give different names to the visit/accept methods, otherwise the names
clash and generate an error._

## **2. Singleton**
Singleton is located in wrappeddatabase/Statistics. Singleton Pattern was a good approach because the Database for Wrapped was 
necessary at all times, therefore it was mandatory to be global. It helped by making any statistic available in for any
class. (Note: if I were to start this homework from scratch, I would put everything in Singletons in order to increase 
program flexibility and overall organisation).

## **3. Factory**
Factory is located in wrappeddatabase/StatsFactory. Factory Pattern was helpful when creating new users, because it automatically
updated the Wrapped Database with the required user. I switched things up a bit by automatically updating the Statistics
arrays instead of returning the new object in order to make the whole creation process more compact.

## **4.Builder**
Builder is located within monetization/ArtistRevenue. Builder Pattern was a choice of readability; I decided to implement
it because I felt like it would make the creation of ArtistRevenue instance more clear and since this class has many
optional fields, it seemed like the best approach for a POO-style implementation.