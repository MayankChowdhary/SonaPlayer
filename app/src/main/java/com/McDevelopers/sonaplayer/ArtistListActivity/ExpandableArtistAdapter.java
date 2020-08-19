package com.McDevelopers.sonaplayer.ArtistListActivity;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ExpandableArtistAdapter extends ExpandableRecyclerViewAdapter<ArtistCheckGroupHolder, ArtistExpandChildHolder> {

    static List<ArtistCategoryExpanded> albumzCategoryExpanded= Collections.emptyList();

    public static final HashMap<String, String> slectedItems = new HashMap<>();
    private static boolean checkFlag=false;
    private static boolean isAnimEnabled=true;


    public ExpandableArtistAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
        albumzCategoryExpanded=(List<ArtistCategoryExpanded>)groups;
        checkFlag=false;
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        isAnimEnabled=currentState.getBoolean("isAnimEnabled",true);
    }

    interface OnItemClickListener{
        void OnItemClick(ExpandableGroup group, int position);
    }
    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemLongClickListener{
        void OnItemLongClick(ExpandableGroup group, int position);
    }
    OnItemLongClickListener onItemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    interface OnGroupClickListenerX{
        void OnGroupClick(ExpandableGroup group, int flatPosition);
    }
    OnGroupClickListenerX onGroupClickListenerX;
    public void setOnGroupClickListenerX(OnGroupClickListenerX onGroupClickListenerX) {
        this.onGroupClickListenerX = onGroupClickListenerX;
    }

    interface OnOptionClickListener{
        void OnOptionClick(ExpandableGroup group, int position);
    }
    OnOptionClickListener onOptionClickListener;
    public void setOnOptionClickListener(OnOptionClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
    }

    interface OnGroupLongClickListenerX{
        void OnGroupLongClick(ExpandableGroup group, int flatPosition);
    }
    OnGroupLongClickListenerX onGroupLongClickListenerX;
    public void setOnGroupLongClickListenerX(OnGroupLongClickListenerX onGroupLongClickListenerX) {
        this.onGroupLongClickListenerX = onGroupLongClickListenerX;
    }

    @Override
    public ArtistCheckGroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_category_view, parent, false);
        return new ArtistCheckGroupHolder(view);
    }

    @Override
    public ArtistExpandChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_view, parent, false);
        return new ArtistExpandChildHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final ArtistExpandChildHolder holder, int flatPosition,
                                      final ExpandableGroup group, final int childIndex) {


        final Artists artist = ((ArtistCategoryExpanded) group).getItems().get(childIndex);
        holder.setArtistName(artist.getName());
        holder.setChildAlbumArt(artist.getData(),checkFlag);

        holder.setMarkBox(checkFlag);

        if(checkFlag){

            if(slectedItems.containsKey(artist.getMediaIds())) {
                holder.setMarkBoxCheck(true);
            }else {

                holder.setMarkBoxCheck(false);

            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onItemClickListener!=null) {
                    onItemClickListener.OnItemClick(group, childIndex);


                }
            }});

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(onItemLongClickListener!=null){
                    onItemLongClickListener.OnItemLongClick(group,childIndex);
                }

                return false;
            }
        });

        ImageButton optionButton= holder.itemView.findViewById(R.id.option_button);
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onOptionClickListener.OnOptionClick(group, childIndex);
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(final ArtistCheckGroupHolder holder, final int flatPosition,
                                      final ExpandableGroup group) {

        ArtistCategoryExpanded albumCategoryExpanded=(ArtistCategoryExpanded)group;

        holder.setGenreTitle(group);
        holder.setGroupIcon(albumCategoryExpanded.getAlbumUris(),checkFlag,albumCategoryExpanded.getSongdata());
        if(isAnimEnabled && !checkFlag)
            animate(holder);

       /* holder.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(int flatPos) {
                toggleGroup(flatPos);
                if(onGroupClickListenerX!=null) {
                    onGroupClickListenerX.OnGroupClick(group, flatPos);
                }

                Log.d("GroupClicked", "onGroupClick: FlatPos: "+flatPos);
                return true;
            }
        });*/

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (!isGroupExpanded(flatPosition)) {

                    toggleGroup(flatPosition);
                }

                if(onGroupLongClickListenerX!=null) {
                    onGroupLongClickListenerX.OnGroupLongClick(group, flatPosition);
                }

                return true;
            }
        });

    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(), R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    void setCheckFlag(boolean check){
        checkFlag=check;
        slectedItems.clear();
        Log.d("SettingNewCheckFlag", "itemStateArrayCleared ");
    }


    String getMarkCount(){
        String markCountText;
        markCountText=slectedItems.size()+" / "+getTotalCount();

        return markCountText;

    }

    int getmarkedNumCount(){

        return slectedItems.size();
    }

     int getTotalCount(){

        int totalAlbumCount=0;
         int songNum;

         for (int i=0;i<albumzCategoryExpanded.size();i++) {

              songNum = albumzCategoryExpanded.get(i).getItems().size();
             totalAlbumCount+=songNum;

         }
         Log.d("TotalAlbumCount", "getTotalCount: "+totalAlbumCount);

         return totalAlbumCount;
    }

    HashMap<String, String> getMarkedFileList(){

        return slectedItems;
    }

    void  setMarkBox(ExpandableGroup group,int childIndex){

        ArtistCategoryExpanded albumCategoryExpanded = (ArtistCategoryExpanded) group;
        List<Artists> albums = albumCategoryExpanded.getSongName();
        String mediaId=albums.get(childIndex).getMediaIds();

        if(slectedItems.containsKey(mediaId)){

            slectedItems.remove(mediaId);

        }else {

            slectedItems.put(mediaId,albums.get(childIndex).getData());
            Log.d("MarkReflected",albums.get(childIndex).getName() );

        }
            notifyDataSetChanged();
    }

    void  setMarkGroup(ExpandableGroup group){

        ArtistCategoryExpanded albumCategoryExpanded = (ArtistCategoryExpanded) group;
        List<Artists> albumsList = albumCategoryExpanded.getSongName();
       boolean allMarked=true;
        String mediaId;

        for ( Artists albums: albumsList) {
          mediaId =albums.getMediaIds();
          if(!slectedItems.containsKey(mediaId)) {
              allMarked = false;
              break;
          }
        }

      if(allMarked){

          for ( Artists albums: albumsList) {
              mediaId =albums.getMediaIds();
              slectedItems.remove(mediaId);
          }
      }else {

          for ( Artists albums: albumsList) {
              mediaId =albums.getMediaIds();
              slectedItems.put(mediaId,albums.getData());
          }
      }

        notifyDataSetChanged();
    }


    void setMarkAll(boolean isChecked) {

        if (isChecked) {

            slectedItems.clear();
            List<Artists> songTemp;
            String mediaId;
            String data;

            // case insensitive search
            for (ArtistCategoryExpanded song : albumzCategoryExpanded) {

                songTemp = song.getSongName();

                for (Artists songz : songTemp) {
                    mediaId = songz.getMediaIds();
                    data = songz.getData();
                    slectedItems.put(mediaId, data);

                    Log.d("MarkAll", "AlbumData: " + data);
                }

                notifyDataSetChanged();
            }
        }else {

            slectedItems.clear();
            notifyDataSetChanged();

        }
    }

}