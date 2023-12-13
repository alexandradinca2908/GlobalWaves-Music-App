package main.SelectionClasses.Creators;

import fileio.input.UserInput;
import main.SelectionClasses.ItemSelection;

public final class HostSelection extends ItemSelection {
    private UserInput host;

    public HostSelection() {
    }

    public UserInput getHost() {
        return host;
    }

    public void setHost(UserInput host) {
        this.host = host;
    }
}
