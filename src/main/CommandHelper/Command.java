package main.CommandHelper;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;

import java.util.ArrayList;

public final class Command {
    private String command = null;
    private String username;
    private int timestamp;
    private String type;
    private Filters filters;
    private int itemNumber;
    private String playlistName;
    private int playlistId;
    private int seed;
    //  Add user
    private int age;
    private String city;
    //  Add album
    private String name;
    private int releaseYear;
    private String description;
    private ArrayList<SongInput> songs = new ArrayList<>();
    //  Add event
    private String date;
    //  Add merch
    private int price;
    //  Add episodes
    private ArrayList<EpisodeInput> episodes = new ArrayList<>();
    //  Change page
    private String nextPage;
    //  Update recommendations
    private String recommendationType;

    public Command() {
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(final Filters filters) {
        this.filters = filters;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(final int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }
}
