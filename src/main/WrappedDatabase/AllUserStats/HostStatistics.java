package main.WrappedDatabase.AllUserStats;

import fileio.input.UserInput;

public final class HostStatistics extends GeneralStatistics {
    private UserInput host;

    public UserInput getHost() {
        return host;
    }

    public void setHost(UserInput host) {
        this.host = host;
    }
}
