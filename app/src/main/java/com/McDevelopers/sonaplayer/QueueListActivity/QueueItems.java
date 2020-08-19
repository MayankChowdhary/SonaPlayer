package com.McDevelopers.sonaplayer.QueueListActivity;
import java.io.Serializable;

public class QueueItems  implements Serializable {
    public String title;
    public long duration;
    public String size;
    public String data;
    public String mediaId;
    public  String filename;

     public QueueItems(String title, long duration, String size,String data,String mediaId,String filename){
        this.title=title;
        this.duration=duration;
        this.data=data;
        this.size=size;
        this.mediaId=mediaId;
        this.filename=filename;
    }
}
