package com.McDevelopers.sonaplayer.RecentlyAddedActivity;

public class RecentData {


        public String title;
        public String description;
        public String imageId;
        public String mediaId;
        public String fileName;
        public String data;
        public Long duration;
        public String size;
        public String date;

        RecentData(String title, String description, String imageId,String mediaId,String data,String fileName,Long duration,String size,String date) {
            this.title = title;
            this.description = description;
            this.imageId = imageId;
            this.mediaId = mediaId;
            this.data = data;
            this.fileName = fileName;
            this.duration = duration;
            this.size = size;
            this.date = date;



        }
    }
