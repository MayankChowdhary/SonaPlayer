package com.McDevelopers.sonaplayer.videolist;

import java.io.Serializable;

/**
 * Created by I'M Slave on 5/3/2018.
 */

public class VideoData implements Serializable {

    public String title;
    public String imageId;
    public String mediaId;
    public  String description;
    public Long duration;
    public String size;
    public String filename;

    public VideoData(String title, String imageId, String mediaId, String description,Long duration,String size, String filename) {
        this.title = title;
        this.imageId = imageId;
        this.mediaId=mediaId;
        this.description=description;
        this.duration=duration;
        this.size=size;
        this.filename=filename;
    }


}