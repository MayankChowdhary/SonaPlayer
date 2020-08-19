package com.McDevelopers.sonaplayer.ArtistListActivity;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ArtistCategoryExpanded extends ExpandableGroup<Artists> {

    private  int albumId;
    private String mName;
    private List<Artists> mAlbum;
    private String albumUris;
    private String songCounts;
    private String songdata;

    public ArtistCategoryExpanded(Long albumId, String title, String albumUri, List<Artists> items, String songCount,String songData) {
        super(title, items);
        this.albumId= albumId.intValue();
        mName = title;
        mAlbum = items;
        albumUris=albumUri;
        songCounts=songCount;
        this.songdata=songData;
    }

    public String getName() {
        return mName;
    }

    public String getSongdata() {
        return songdata;
    }


    public String getSongCount() {
        return songCounts;
    }

    public  List<Artists> getSongName() {
        return mAlbum;
    }

    public String getAlbumUris() {
        return albumUris;
    }

    public int getAlbumId() {
        return albumId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtistCategoryExpanded)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        return albumId;
    }
}