package com.McDevelopers.sonaplayer.MediaListActivity;

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
import com.McDevelopers.sonaplayer.Data;
import com.McDevelopers.sonaplayer.GlideApp;
import com.McDevelopers.sonaplayer.MediaDataId;
import com.McDevelopers.sonaplayer.MusicLibrary;
import com.McDevelopers.sonaplayer.QueueListActivity.QueueItems;
import com.McDevelopers.sonaplayer.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class Recycler_View_Adapter_Grid extends RecyclerView.Adapter<View_Holder_Grid> implements SectionIndexer {

    List<Data> list;
    private Context context;
    private int scroll;
    private static  int currentArtIndex;
    private static boolean isCustomArt = false;
    private static boolean isAnimEnabled=true;
    private static boolean checkFlag=false;
    private static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private static List<MediaDataId> markedFileList=new ArrayList<>();
    private   List<QueueItems> queueItemHashMap=new ArrayList<>();

    private DrawableCrossFadeFactory factory ;



    public Recycler_View_Adapter_Grid(List<Data> list, Context context) {
        this.list = list;
        this.context = context;
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        isCustomArt = currentState.getBoolean("customAlbum", false);
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
    public View_Holder_Grid onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridlist_item, parent, false);
        View_Holder_Grid holder = new View_Holder_Grid(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder_Grid holder, int position) {

        if ((list.get(position).title).length() > 15) {
            holder.title.setText(list.get(position).title);
        } else {

            holder.title.setText(list.get(position).title + " - " + list.get(position).fileName);
        }
        holder.title.setSelected(false);
        holder.audioMeta.setText(timeConvert(list.get(position).duration) + " | " + getSongFormat(list.get(position).data)+" | "+list.get(position).size);

        if (isVideoFile(list.get(position).data)){

            holder.videoIcon.setVisibility(View.VISIBLE);
        }else {
            holder.videoIcon.setVisibility(View.GONE);

        }

        if(isAnimEnabled && !checkFlag)
        animate(holder);

        if(checkFlag) {
            holder.markBox.setVisibility(View.VISIBLE);
            if (!itemStateArray.get(position, false)) {
                holder.markBox.setChecked(false);}
            else {
                holder.markBox.setChecked(true);
            }
        }
        else if(holder.markBox.getVisibility()==View.VISIBLE)
            holder.markBox.setVisibility(View.GONE);

        if (isCustomArt && MusicLibrary.listOfAllImages.size()!=0 && !checkFlag) {

            GlideApp
                    .with(holder.imageView.getContext())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .load(new File(randomArt()))
                    .transition(withCrossFade(factory))
                    .placeholder(R.drawable.default_album)
                    .error(R.drawable.default_album)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imageView);
        }else {

            if (isVideoFile(list.get(position).data)) {
                GlideApp
                        .with(holder.imageView.getContext())
                        .asBitmap()
                        .load(Uri.fromFile(new File(list.get(position).data)))
                        .transition(withCrossFade(factory))
                        .placeholder(R.drawable.default_album)
                        .error(R.drawable.default_album)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.imageView);
            } else {

                GlideApp
                        .with(context)
                        .asBitmap()
                        .load(list.get(position).data)
                        .placeholder(R.drawable.default_album)
                        .transition(withCrossFade(factory))
                        .signature(new ObjectKey(new File(list.get(position).data).lastModified()))
                        .error(R.drawable.default_album)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.imageView);
            }
        }

        if (scroll == position) {
            holder.vuMeterView.setVisibility(View.VISIBLE);
            holder.vuMeterView.resume(true);

        } else {

            holder.vuMeterView.setVisibility(View.GONE);
        }

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

    public void updateList(List<Data> listnew){
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
                MediaDataId mediaDataId=new MediaDataId(list.get(j).mediaId,list.get(j).data);
                markedFileList.add(mediaDataId);
                Log.d("FileMarked", "path: "+list.get(j).data);
            }
        }

        return markedFileList;
    }

    List<QueueItems> getMarkedIds(){
        queueItemHashMap.clear();
        Log.d("EnteredInGetMarkedItems", "getMarkedItems: itemArrayState Size: "+itemStateArray.size());


        for (int j=0;j<list.size();j++){
            if(itemStateArray.get(j,false)){
                QueueItems queueItems=new QueueItems(list.get(j).title,list.get(j).duration,list.get(j).size,list.get(j).data,list.get(j).mediaId,list.get(j).fileName);
                queueItemHashMap.add(queueItems);
                Log.d("FileMarked", "path: "+list.get(j).data);
            }
        }

        return queueItemHashMap;
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Data data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Data data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }
    private static String randomArt() {

        int max = MusicLibrary.listOfAllImages.size();
        Random rand = new Random();
        int newAlbumIndex=currentArtIndex;

        try {
            if(max>1) {

                if(max>50){
                    while (newAlbumIndex == currentArtIndex) {
                        newAlbumIndex = rand.nextInt(max);
                    }}
                else {

                    newAlbumIndex = rand.nextInt(max);
                }
                String newAlbum = MusicLibrary.listOfAllImages.get(newAlbumIndex);
                currentArtIndex=newAlbumIndex;
                return newAlbum;
            }else
                currentArtIndex=-1;
            return "";
        }catch (Throwable e){

            Log.e("ExceptionRaised", "randomArt: "+e );
            currentArtIndex=-1;
            return "";
        }

    }
    private static boolean isVideoFile(String path) {

        try {

            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        } catch (Throwable e) {
            // Log.e("ExceptionInIsVideo", "isVideoFile: "+e );
            return false;
        }

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
