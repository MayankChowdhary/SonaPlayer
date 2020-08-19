package com.McDevelopers.sonaplayer.FolderListActivity;


import android.os.Parcel;
import android.os.Parcelable;

public class Folders implements Parcelable {

    private String name;
    private String mediaIds;
    private String albumIds;
    private String fileName;
    private String data;
    private  boolean isVideo;
    private String albumName;
    private String artistName;
    private long duration;
    private long size;


    public Folders(String name, String mediaId, String albumId, String filename, String data,boolean isVideo, String albumName, String artistName, long duration, long size) {
        this.name = name;
        this.mediaIds=mediaId;
        this.albumIds=albumId;
        this.fileName=filename;
        this.data=data;
        this.isVideo=isVideo;
        this.albumName=albumName;
        this.artistName=artistName;
        this.duration=duration;
        this.size=size;
    }

    protected Folders(Parcel in) {
        name = in.readString();
        mediaIds=in.readString();
        albumIds=in.readString();
        fileName=in.readString();
        data=in.readString();
        isVideo=Boolean.valueOf(in.readString());
        albumName=in.readString();
        artistName=in.readString();
        duration=in.readLong();
        size=in.readLong();
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
    public boolean getIsVideo() {
        return isVideo;
    }

    public String getAlbumName() {
        return albumName;
    }
    public String getArtistName() {
        return artistName;
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
        if (!(o instanceof Folders)) return false;

        Folders albums = (Folders) o;

        if (!getMediaIds().equals(albums.getMediaIds())) return false;
        if (!getAlbumIds().equals(albums.getAlbumIds())) return false;
        if (!getFileName().equals(albums.getFileName())) return false;
        if (!getData().equals(albums.getData())) return false;
        if (getIsVideo()!=(albums.getIsVideo())) return false;


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
        dest.writeString(String.valueOf(isVideo));
        dest.writeString(albumName);
        dest.writeString(artistName);
        dest.writeLong(duration);
        dest.writeLong(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Folders> CREATOR = new Creator<Folders>() {
        @Override
        public Folders createFromParcel(Parcel in) {
            return new Folders(in);
        }

        @Override
        public Folders[] newArray(int size) {
            return new Folders[size];
        }
    };
}