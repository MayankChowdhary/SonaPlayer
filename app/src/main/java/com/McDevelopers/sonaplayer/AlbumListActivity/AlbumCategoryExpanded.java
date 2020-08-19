package com.McDevelopers.sonaplayer.AlbumListActivity;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class AlbumCategoryExpanded extends ExpandableGroup<Albums> {

    private  int albumId;
    private String mName;
    private List<Albums> mAlbum;
    private String albumUris;
    private String songCounts;
    private String songData;
    private String songAlbumId;

    public AlbumCategoryExpanded(Long albumId,String title, String albumUri, List<Albums> items, String songCount,String songData,String songAlbumId) {
        super(title, items);
        this.albumId= albumId.intValue();
        mName = title;
        mAlbum = items;
        albumUris=albumUri;
        songCounts=songCount;
        this.songData=songData;
        this.songAlbumId=songAlbumId;
    }

    public String getName() {
        return mName;
    }
    public String getSongData() {
        return songData;
    }
    public String getSongAlbumId() {
        return songAlbumId;
    }

    public String getSongCount() {
        return songCounts;
    }

    public  List<Albums> getSongName() {
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
        if (!(o instanceof AlbumCategoryExpanded)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        return albumId;
    }
}