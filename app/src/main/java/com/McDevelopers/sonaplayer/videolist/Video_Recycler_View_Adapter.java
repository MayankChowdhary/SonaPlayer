package com.McDevelopers.sonaplayer.videolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SectionIndexer;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.GlideApp;
import com.McDevelopers.sonaplayer.MediaDataId;
import com.McDevelopers.sonaplayer.QueueListActivity.QueueItems;
import com.McDevelopers.sonaplayer.R;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class Video_Recycler_View_Adapter extends RecyclerView.Adapter<Video_View_Holder> implements SectionIndexer {

    List<VideoData> list;
    private Context context;
    private int scroll;
    private static boolean isAnimEnabled=true;
    private static boolean checkFlag=false;
    private static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private static List<MediaDataId> markedFileList=new ArrayList<>();
    private   List<QueueItems> queueItemHashMap=new ArrayList<>();

    private DrawableCrossFadeFactory factory ;

    private RequestOptions options = new RequestOptions()
           .placeholder(R.drawable.loading_art)
           .error(R.drawable.loading_art)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .priority(Priority.HIGH);



    public Video_Recycler_View_Adapter(List<VideoData> list, Context context) {
        this.list = list;
        this.context = context;
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        isAnimEnabled=currentState.getBoolean("isAnimEnabled",true);
        checkFlag=false;
        itemStateArray.clear();
        factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
    }


    @Override
    public Object[] getSections() {

        return list.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= list.size()) {
            position = list.size() - 1;
        }

        return position;
    }



    @Override
    public Video_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videolist_item, parent, false);
       Video_View_Holder holder = new Video_View_Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(Video_View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        if ((list.get(position).title).length() > 15) {
            holder.title.setText(list.get(position).title);
        } else {

            holder.title.setText(list.get(position).title+" - "+ list.get(position).filename);
        }
        holder.title.setSelected(false);
        //holder.imageView.setImageBitmap(bitmap);
        holder.videoMeta.setText(timeConvert(list.get(position).duration) + " | " + getSongFormat(list.get(position).imageId)+" | "+list.get(position).size);



        if(isAnimEnabled  && !checkFlag)
        animate(holder);

        if(checkFlag) {
            holder.mark_box.setVisibility(View.VISIBLE);
            if (!itemStateArray.get(position, false)) {
                holder.mark_box.setChecked(false);}
            else {
                holder.mark_box.setChecked(true);
            }
        }
        else if(holder.mark_box.getVisibility()==View.VISIBLE)
            holder.mark_box.setVisibility(View.GONE);


        GlideApp
                .with(holder.imageView.getContext())
                .asBitmap()
                .load(Uri.fromFile(new File(list.get(position).imageId)))
                .transition(withCrossFade(factory))
                .apply(options)
                .into(  holder.imageView);



    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public long getItemId(int position){
        return   list.get(position).mediaId.hashCode();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateList(List<VideoData> listnew){
        markedFileList.clear();
        itemStateArray.clear();
        queueItemHashMap.clear();
        list = listnew;
        notifyDataSetChanged();
    }

    public void scrollPos(int pos){
        scroll=pos;
    }

    void setCheckFlag(boolean check){
        checkFlag=check;
        itemStateArray.clear();
        queueItemHashMap.clear();
        markedFileList.clear();
        itemStateArray.clear();
        Log.d("SettingNewCheckFlag", "itemStateArrayCleared ");
    }

    void  setMarkBox(int adapterPosition){
        if (!itemStateArray.get(adapterPosition, false)) {
            itemStateArray.put(adapterPosition, true);
            Log.d("MarkReflected", "setMarkBox: "+itemStateArray.get(adapterPosition, false));
            notifyDataSetChanged();
        }
        else  {
            itemStateArray.put(adapterPosition, false);
            notifyDataSetChanged();
            Log.d("MarkReflected", "setMarkBox: "+itemStateArray.get(adapterPosition, false));

        }

    }

    void setMarkAll(boolean markAll){

        for(int i=0;i<list.size();i++){
            itemStateArray.put(i, markAll);
            // Log.d("MarkAll", "setMarkAll: "+markAll);
        }
        notifyDataSetChanged();
    }


    String getMarkCount(){
        String markCountText;
        int markCount=0;
        for(int i=0;i<list.size();i++){
            if(itemStateArray.get(i, false)){
                markCount++;
            }
        }

        markCountText=markCount+" / "+list.size();

        return markCountText;

    }

    int getmarkedNumCount(){

        int markCount=0;
        for(int i=0;i<list.size();i++){
            if(itemStateArray.get(i, false)){
                markCount++;
            }
        }

        return markCount;
    }

    List<MediaDataId> getMarkedFileList(){
        markedFileList.clear();
        Log.d("EnteredInGetMarkedFiles", "getMarkedFileList: itemArrayState Size: "+itemStateArray.size());


        for (int j=0;j<list.size();j++){
            // Log.d("EnteredInGetMarkedLoop", "getMarkedFileList: itemArrayState value: "+itemStateArray.get(j,false));

            if(itemStateArray.get(j,false)){
                MediaDataId mediaDataId=new MediaDataId(list.get(j).mediaId,list.get(j).imageId);
                markedFileList.add(mediaDataId);
                Log.d("FileMarked", "path: "+list.get(j).imageId);
            }
        }

        return markedFileList;
    }

    List<QueueItems> getMarkedIds(){
        queueItemHashMap.clear();
        Log.d("EnteredInGetMarkedItems", "getMarkedItems: itemArrayState Size: "+itemStateArray.size());


        for (int j=0;j<list.size();j++){
            if(itemStateArray.get(j,false)){
                QueueItems queueItems=new QueueItems(list.get(j).title,list.get(j).duration,list.get(j).size,list.get(j).imageId,list.get(j).mediaId,list.get(j).filename);
                queueItemHashMap.add(queueItems);
                Log.d("FileMarked", "path: "+list.get(j).imageId);
            }
        }

        return queueItemHashMap;
    }
    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, VideoData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(VideoData data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    private static String getSongFormat(String data){

        return  ((data).substring((data.lastIndexOf(".") + 1)).toUpperCase());
    }

    private String timeConvert(long durationX) {
        int duration=(int)(durationX/1000);

        if(duration<3600000) {

            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }else {

            return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

        }

    }

}
