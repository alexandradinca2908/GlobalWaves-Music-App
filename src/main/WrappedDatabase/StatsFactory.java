package main.WrappedDatabase;

import fileio.input.UserInput;
import main.WrappedDatabase.AllUserStats.ArtistStatistics;
import main.WrappedDatabase.AllUserStats.HostStatistics;
import main.WrappedDatabase.AllUserStats.UserStatistics;

public final class StatsFactory {
    public static void createStats(final UserInput user) {
        switch (user.getType()) {
            case "user" -> {
                UserStatistics statistics = new UserStatistics(user);
                Statistics.getWrappedStats().getUsersStatistics().add(statistics);
            }
            case "artist" -> {
                ArtistStatistics statistics = new ArtistStatistics(user);
                Statistics.getWrappedStats().getArtistsStatistics().add(statistics);
            }
            case "host" -> {
                HostStatistics statistics = new HostStatistics(user);
                Statistics.getWrappedStats().getHostsStatistics().add(statistics);
            }
        }
    }
}
