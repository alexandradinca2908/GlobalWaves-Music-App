package main.CommandHelper;

import fileio.input.UserInput;

import java.util.ArrayList;

public final class Search {
    private String user;
    private ArrayList<String> lastSearchResult = new ArrayList<>();
    int[] steps = new int[2];

    public Search() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getLastSearchResult() {
        return lastSearchResult;
    }

    public void setLastSearchResult(final ArrayList<String> lastSearchResult) {
        this.lastSearchResult = lastSearchResult;
    }

    public int[] getSteps() {
        return steps;
    }

    public void setSteps(final int step1, final int step2) {
        steps[0] = step1;
        steps[1] = step2;
    }
}
