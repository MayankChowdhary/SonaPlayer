package com.McDevelopers.sonaplayer.ArtistListActivity;


import android.os.Parcel;
import android.os.Parcelable;

public class Artists implements Parcelable {

    private String name;
    private String mediaIds;
    private String albumIds;
    private String fileName;
    private String data;
    private String albumName;
    private long duration;
    private long size;


    public Artists(String name, String mediaId, String albumId, String filename, String data,String album,long duration,long size ) {
        this.name = name;
        this.mediaIds=mediaId;
        this.albumIds=albumId;
        this.fileName=filename;
        this.data=data;
        this.albumName=album;
        this.duration=duration;
        this.size=size;
    }

    protected Artists(Parcel in) {
        name = in.readString();
        mediaIds=in.readString();
        albumIds=in.readString();
        fileName=in.readString();
        data=in.readString();
        albumName=in.readString();
    }

    public String getName() {
        if(name.length()>15) {
            return name;
        }
        else{
            return name+" - "+fileName;
        }
    }
    public String getMediaIds() {
        return mediaIds;
    }
    public String getAlbumIds() {
        return albumIds;
    }

    public String getFileName() {
        return fileName;
    }

    public String getData() {
        return data;
    }
    public String getAlbumName() {
        return albumName;
    }

    public long getDuration() {
        return duration;
    }
    public long getSize() {
        return size;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artists)) return false;

        Artists albums = (Artists) o;

        if (!getMediaIds().equals(albums.getMediaIds())) return false;
        if (!getAlbumIds().equals(albums.getAlbumIds())) return false;
        if (!getFileName().equals(albums.getFileName())) return false;
        if (!getData().equals(albums.getData())) return false;


        return getName() != null ? getName().equals(albums.getName()) : albums.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getMediaIds() != null ? getMediaIds().hashCode() : 0);
        result = 31 * result + (getAlbumIds() != null ? getAlbumIds().hashCode() : 0);
        result = 31 * result + (getFileName() != null ? getFileName().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);

        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mediaIds);
        dest.writeString(albumIds);
        dest.writeString(fileName);
        dest.writeString(data);
        dest.writeString(albumName);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artists> CREATOR = new Creator<Artists>() {
        @Override
        public Artists createFromParcel(Parcel in) {
            return new Artists(in);
        }

        @Override
        public Artists[] newArray(int size) {
            return new Artists[size];
        }
    };
}