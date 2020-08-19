package com.McDevelopers.sonaplayer.AlbumListActivity;


import android.os.Parcel;
import android.os.Parcelable;

public class Albums implements Parcelable {

    private String name;
    private String mediaIds;
    private String albumIds;
    private String fileName;
    private String data;
    private long duration;
    private long size;
    private String artist;


    public Albums(String name, String mediaId, String albumId, String filename,String data,long duration,long size,String artist ) {
        this.name = name;
        this.mediaIds=mediaId;
        this.albumIds=albumId;
        this.fileName=filename;
        this.data=data;
        this.duration=duration;
        this.size=size;
        this.artist=artist;
    }

    protected Albums(Parcel in) {
        name = in.readString();
        mediaIds=in.readString();
        albumIds=in.readString();
        fileName=in.readString();
        data=in.readString();
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
    public long getDuration() {
        return duration;
    }
    public long getSize() {
        return size;
    }
    public String getArtist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Albums)) return false;

        Albums albums = (Albums) o;

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
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(artist);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Albums> CREATOR = new Creator<Albums>() {
        @Override
        public Albums createFromParcel(Parcel in) {
            return new Albums(in);
        }

        @Override
        public Albums[] newArray(int size) {
            return new Albums[size];
        }
    };
}