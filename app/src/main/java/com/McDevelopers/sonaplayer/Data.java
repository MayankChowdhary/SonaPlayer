package com.McDevelopers.sonaplayer;

import java.io.Serializable;

/**
 * Created by I'M Slave on 5/3/2018.
 */

public class Data implements Serializable {

    public String title;
    public String description;
    public String imageId;
    public String mediaId;
    public String fileName;
    public String data;
    public Long duration;
    public String size;

    Data(String title, String description, String imageId,String mediaId,String data,String fileName,Long duration,String size) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.mediaId=mediaId;
        this.data=data;
        this.fileName=fileName;
        this.duration=duration;
        this.size=size;
    }


}