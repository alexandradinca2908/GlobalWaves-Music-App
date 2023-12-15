package main.LikeClasses;

import fileio.input.UserInput;

public final class ArtistLikes {
    private UserInput user;
    private int likes;

    public ArtistLikes() {
    }

    public UserInput getUser() {
        return user;
    }

    public void setUser(final UserInput user) {
        this.user = user;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(final int likes) {
        this.likes = likes;
    }
}
