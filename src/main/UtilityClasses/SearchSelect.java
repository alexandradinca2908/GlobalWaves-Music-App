package main.UtilityClasses;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import main.CommandHelper.Command;
import main.CommandHelper.Filters;
import main.CommandHelper.Search;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.SelectionClasses.*;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;

import java.util.ArrayList;
public final class SearchSelect {
    private SearchSelect() {
    }

    /**
     * This command filters the songs based on the search filter
     *
     * @param filters Search filters
     * @param result Found songs
     * @param library Library containing all the songs
     */
    public static void searchForSongs(final Filters filters,
                                      final ArrayList<SongInput> result,
                                      final LibraryInput library) {
        //  Add all songs containing the searched name
        if (filters.getName() != null) {
            for (SongInput song : library.getSongs()) {
                if (song.getName().startsWith(filters.getName())) {
                    result.add(song);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from unwanted albums
        if (result.isEmpty()) {
            if (filters.getAlbum() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getAlbum().equals(filters.getAlbum())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getAlbum() != null) {
                result.removeIf(song -> !song.getAlbum().equals(filters.getAlbum()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs with unwanted tags
        if (result.isEmpty()) {
            if (filters.getTags() != null) {
                for (SongInput song : library.getSongs()) {
                    int hasTags = 1;
                    for (String tag : filters.getTags()) {
                        if (!song.getTags().contains(tag)) {
                            hasTags = 0;
                            break;
                        }
                    }
                    if (hasTags == 1) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getTags() != null) {
                for (SongInput song : result) {
                    for (String tag : filters.getTags()) {
                        if (!song.getTags().contains(tag)) {
                            result.remove(song);
                            break;
                        }
                    }
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs with mismatched lyrics
        if (result.isEmpty()) {
            if (filters.getLyrics() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getLyrics().toLowerCase().contains(
                            filters.getLyrics().toLowerCase())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getLyrics() != null) {
                result.removeIf(song -> !song.getLyrics().contains(filters.getLyrics()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from wrong genre
        if (result.isEmpty()) {
            if (filters.getGenre() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getGenre().equalsIgnoreCase(filters.getGenre())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getGenre() != null) {
                result.removeIf(song -> !song.getGenre().equalsIgnoreCase(filters.getGenre()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from wrong years
        if (result.isEmpty()) {
            if (filters.getReleaseYear() != null) {
                //  Extract the </> operator and the year from original filter
                char op = filters.getReleaseYear().charAt(0);
                int year = Integer.parseInt(filters.getReleaseYear().substring(1));

                if (op == '>') {
                    for (SongInput song : library.getSongs()) {
                        if (song.getReleaseYear() > year) {
                            result.add(song);
                        }
                    }
                } else {
                    for (SongInput song : library.getSongs()) {
                        if (song.getReleaseYear() < year) {
                            result.add(song);
                        }
                    }
                }

            }
        } else {
            if (filters.getReleaseYear() != null) {
                //  Extract the </> operator and the year from original filter
                char op = filters.getReleaseYear().charAt(0);
                int year = Integer.parseInt(filters.getReleaseYear().substring(1));

                if (op == '>') {
                    result.removeIf(song -> song.getReleaseYear() < year);
                } else {
                    result.removeIf(song -> song.getReleaseYear() > year);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from other artists
        if (result.isEmpty()) {
            if (filters.getArtist() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getArtist().equals(filters.getArtist())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getArtist() != null) {
                result.removeIf(song -> !song.getArtist().equals(filters.getArtist()));
            }
        }
    }

    /**
     * This command filters the playlists based on the search filter
     *
     * @param filters Search filters
     * @param result Found playlists
     * @param playlists The array of all user playlists
     */
    public static void searchForPlaylists(final Filters filters,
                                          final ArrayList<Playlist> result,
                                          final ArrayList<Playlist> playlists) {
        //  Add all playlists containing the searched name
        if (filters.getName() != null) {
            for (Playlist playlist : playlists) {
                if (playlist.getName().startsWith(filters.getName())) {
                    result.add(playlist);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove playlists from other owners
        if (result.isEmpty()) {
            if (filters.getOwner() != null) {
                for (Playlist playlist : playlists) {
                    if (playlist.getOwner().equals(filters.getOwner())) {
                        result.add(playlist);
                    }
                }
            }
        } else {
            if (filters.getOwner() != null) {
                result.removeIf(playlist -> !playlist.getOwner().equals(filters.getOwner()));
            }
        }
    }

    /**
     * This command filters the podcasts based on the search filter
     *
     * @param filters Search filters
     * @param result Found podcasts
     * @param library Library containing all the songs
     */
    public static void searchForPodcasts(final Filters filters,
                                         final ArrayList<PodcastInput> result,
                                         final LibraryInput library) {
        //  Add all playlists containing the searched name
        if (filters.getName() != null) {
            for (PodcastInput podcast : library.getPodcasts()) {
                if (podcast.getName().startsWith(filters.getName())) {
                    result.add(podcast);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from other owners
        if (result.isEmpty()) {
            if (filters.getOwner() != null) {
                for (PodcastInput podcast : library.getPodcasts()) {
                    if (podcast.getOwner().equals(filters.getOwner())) {
                        result.add(podcast);
                    }
                }
            }
        } else {
            if (filters.getOwner() != null) {
                result.removeIf(podcast -> !podcast.getOwner().equals(filters.getOwner()));
            }
        }
    }

    /**
     * This command filters the albums based on the search filter
     *
     * @param filters Search filters
     * @param result Found albums
     * @param albums The array of all albums
     */
    public static void searchForAlbums(final Filters filters,
                                       final ArrayList<Album> result,
                                       final ArrayList<Album> albums) {
        //  Add all albums containing the searched name
        if (filters.getName() != null) {
            for (Album album : albums) {
                if (album.getName().startsWith(filters.getName())) {
                    result.add(album);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove albums from other owners
        if (result.isEmpty()) {
            if (filters.getOwner() != null) {
                for (Album album : albums) {
                    if (album.getOwner().equals(filters.getOwner())) {
                        result.add(album);
                    }
                }
            }
        } else {
            if (filters.getOwner() != null) {
                result.removeIf(album -> !album.getOwner().equals(filters.getOwner()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove albums with other descriptions
        if (result.isEmpty()) {
            if (filters.getDescription() != null) {
                for (Album album : albums) {
                    if (album.getDescription().equals(filters.getDescription())) {
                        result.add(album);
                    }
                }
            }
        } else {
            if (filters.getDescription() != null) {
                result.removeIf(album -> !album.getDescription().equals(filters.getDescription()));
            }
        }
    }

    public static void searchForCreators(final Filters filters,
                                        final ArrayList<UserInput> result,
                                        final LibraryInput library) {
        //  Add all artists containing the searched name
        if (filters.getName() != null) {
            for (UserInput artist : library.getUsers()) {
                if (artist.getUsername().startsWith(filters.getName())) {
                    result.add(artist);
                }
            }
        }
    }

    public static ArrayList<String> setCreatorSearchResults(final Command crtCommand,
                                                            final LibraryInput library,
                                                            final ObjectNode searchOutput) {
        Filters filters = crtCommand.getFilters();
        ArrayList<UserInput> result = new ArrayList<>();

        //  Found artists will be added in result array
        SearchSelect.searchForCreators(filters, result, library);

        //  Truncate results if needed
        if (result.size() > Constants.MAX_SIZE_5) {
            result.subList(Constants.MAX_SIZE_5, result.size()).clear();
        }

        //  Setting the message
        searchOutput.put("message", "Search returned "
                + result.size() + " results");

        //  Extracting the names of the artists
        ArrayList<String> creatorNames = new ArrayList<>();
        for (UserInput artist : result) {
            creatorNames.add(artist.getUsername());
        }
        searchOutput.putPOJO("results", creatorNames);

        return creatorNames;
    }

    /**
     * This method retrieves the selected song
     *
     * @param crtCommand The select command with all its data
     * @param library Library containing all the songs
     * @param lastSearchResult The array containing the search result and its type
     * @return The selected song
     */
    public static SongSelection getSongSelection(final Command crtCommand,
                                                 final LibraryInput library,
                                                 final ArrayList<String> lastSearchResult) {
        SongSelection selectedSong = new SongSelection();
        //  Set song
        for (SongInput song : library.getSongs()) {
            if (song.getName().equals(lastSearchResult.get(1))) {
                selectedSong.setSong(song);
                break;
            }
        }

        //  Set user
        selectedSong.setUser(crtCommand.getUsername());
        //  Set start time
        selectedSong.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedSong.setRemainingTime(selectedSong.getSong().getDuration());

        return selectedSong;
    }

    /**
     * This method retrieves the selected playlist
     *
     * @param crtCommand The select command with all its data
     * @param playlists The array of all user playlists
     * @param lastSearchResult The array containing the search result and its type
     * @return The selected playlist
     */
    public static PlaylistSelection getPlaylistSelection(final Command crtCommand,
                                                         final ArrayList<Playlist> playlists,
                                                         final ArrayList<String> lastSearchResult) {
        PlaylistSelection selectedPlaylist = new PlaylistSelection();
        //  Set name
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(lastSearchResult.get(1))) {
                selectedPlaylist.setPlaylist(playlist);
                break;
            }
        }
        //  Set user
        selectedPlaylist.setUser(crtCommand.getUsername());
        //  Set start time
        selectedPlaylist.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedPlaylist.setRemainingTime(selectedPlaylist.getPlaylist().getDuration());

        return selectedPlaylist;
    }


    /**
     * This method retrieves the selected podcast
     *
     * @param crtCommand The select command with all its data
     * @param library Library containing all the podcasts
     * @param lastSearchResult The array containing the search result and its type
     * @return The selected podcast
     */
    public static PodcastSelection getPodcastSelection(final Command crtCommand,
                                                       final LibraryInput library,
                                                       final ArrayList<String> lastSearchResult) {
        PodcastSelection selectedPodcast = new PodcastSelection();
        //  Set name
        for (PodcastInput podcast : library.getPodcasts()) {
            if (podcast.getName().equals(lastSearchResult.get(1))) {
                selectedPodcast.setPodcast(podcast);
                break;
            }
        }
        //  Set user
        selectedPodcast.setUser(crtCommand.getUsername());
        //  Set start time
        selectedPodcast.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedPodcast.setRemainingTime(selectedPodcast.getPodcast().getDuration());

        return selectedPodcast;
    }

    /**
     * This method retrieves the selected album
     *
     * @param crtCommand The select command with all its data
     * @param albums The array of all albums
     * @param lastSearchResult The array containing the search result and its type
     * @return The selected album
     */
    public static AlbumSelection getAlbumSelection(final Command crtCommand,
                                                   final ArrayList<Album> albums,
                                                   final ArrayList<String> lastSearchResult) {
        AlbumSelection selectedAlbum = new AlbumSelection();
        //  Set name
        for (Album album : albums) {
            if (album.getName().equals(lastSearchResult.get(1))) {
                selectedAlbum.setAlbum(album);
                break;
            }
        }
        //  Set user
        selectedAlbum.setUser(crtCommand.getUsername());
        //  Set start time
        selectedAlbum.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedAlbum.setRemainingTime(selectedAlbum.getAlbum().getDuration());

        return selectedAlbum;
    }

    /**
     * This method stores the search result in an array for the select command
     *
     * @param crtSearch The object that stores search data
     * @param names The names of the searched items
     * @param type The type of item (song/playlist/podcast)
     */
    public static void storeResultForSelect(final Search crtSearch,
                                            final ArrayList<String> names,
                                            final String type) {
        //  First element specifies the type of items searched
        if (!names.isEmpty()) {
            crtSearch.getLastSearchResult().add(type);
            crtSearch.getLastSearchResult().addAll(names);
        }
    }
}
