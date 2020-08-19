package com.McDevelopers.sonaplayer.FolderListActivity;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class FolderCategoryExpanded extends ExpandableGroup<Folders> {

    private String mName;
    private List<Folders> mAlbum;
    private String songCounts;
    private  boolean isVideo;

    public FolderCategoryExpanded( String title, List<Folders> items, String songCount,boolean isVideo) {
        super(title, items);
        mName = title;
        mAlbum = items;
        songCounts=songCount;
        this.isVideo=isVideo;
    }

    public String getName() {
        return mName;
    }

    public String getSongCount() {
        return songCounts;
    }
    public boolean getIsVideo() {
        return isVideo;
    }

    public  List<Folders> getSongName() {
        return mAlbum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FolderCategoryExpanded)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}