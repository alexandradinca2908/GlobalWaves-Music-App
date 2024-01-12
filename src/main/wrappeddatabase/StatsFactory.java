package main.wrappeddatabase;

import fileio.input.UserInput;
import main.wrappeddatabase.alluserstats.ArtistStatistics;
import main.wrappeddatabase.alluserstats.HostStatistics;
import main.wrappeddatabase.alluserstats.UserStatistics;

public final class StatsFactory {
    private StatsFactory() {
    }

    /**
     * Based on the user type, creates an instance of user statistics
     * @param user User
     */
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
            default -> {
            }
        }
    }
}
