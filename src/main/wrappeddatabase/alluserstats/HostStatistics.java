package main.wrappeddatabase.alluserstats;

import fileio.input.UserInput;

public final class HostStatistics extends GeneralStatistics {
    private UserInput host;

    public HostStatistics(final UserInput host) {
        this.host = host;
    }

    public UserInput getHost() {
        return host;
    }

    public void setHost(final UserInput host) {
        this.host = host;
    }
}
