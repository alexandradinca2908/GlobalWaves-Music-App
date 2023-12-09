package main.PagingClasses;

import main.PlaylistClasses.UserPlaylists;

import java.util.ArrayList;

public final class PageSystem {
    private ArrayList<Page> allPages = new ArrayList<>();

    private static PageSystem pageSystems;

    private PageSystem(){
    }

    public static PageSystem getPageSystems() {
        if (pageSystems == null) {
            pageSystems = new PageSystem();
        }

        return pageSystems;
    }

    public ArrayList<Page> getAllPages() {
        return allPages;
    }

    public void setAllPages(final ArrayList<Page> allPages) {
        this.allPages = allPages;
    }
}
