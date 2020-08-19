package com.McDevelopers.sonaplayer.QueueListActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.GlideApp;
import com.McDevelopers.sonaplayer.MusicLibrary;
import com.McDevelopers.sonaplayer.QueueListActivity.helper.ItemTouchHelperAdapter;
import com.McDevelopers.sonaplayer.QueueListActivity.helper.ItemTouchHelperViewHolder;
import com.McDevelopers.sonaplayer.QueueListActivity.helper.OnStartDragListener;
import com.McDevelopers.sonaplayer.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.gresse.hugo.vumeterlibrary.VuMeterView;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    protected static List<QueueItems> mItems ;
    private final OnStartDragListener mDragStartListener;

    private static boolean isCustomArt = false;
    private static Random rand;
    private static  int currentArtIndex;
    private static boolean isAnimEnabled=true;
    private static boolean checkFlag=false;
    private DrawableCrossFadeFactory factory ;
    private int scroll=-1;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
            mItems=MusicLibrary.queueItems;
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        isCustomArt = currentState.getBoolean("customAlbum", false);
        isAnimEnabled=currentState.getBoolean("isAnimEnabled",true);
        checkFlag=false;
        factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    }

    interface OnItemClickListener{
        void OnItemClick(int position);
    }
    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemDismissListener{
        void OnItemDismiss(int position);
    }
    OnItemDismissListener onItemDismissListener;
    public void setOnItemDismissListener(OnItemDismissListener onItemDismissListener) {
        this.onItemDismissListener = onItemDismissListener;
    }


    interface OnItemMoveListener{
        void OnItemMove(int fromPosition, int toPosition);
    }
    OnItemMoveListener onItemMoveListener;
    public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener) {
        this.onItemMoveListener = onItemMoveListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        boolean isVideoFile=isVideoFile(mItems.get(position).data);

        if ((mItems.get(position).title).length() > 15) {
            holder.textView.setText(mItems.get(position).title);
        } else {

            holder.textView.setText(mItems.get(position).title + " - " + mItems.get(position).filename);
        }
        holder.songMeta.setText(timeConvert(mItems.get(position).duration) + " | " + getSongFormat(mItems.get(position).data)+" | "+ mItems.get(position).size);

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }

                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("ItemsClicked", "onClick: "+position+" : "+mItems.get(position));
                if(onItemClickListener!=null) {
                    onItemClickListener.OnItemClick(position);

                }
            }
        });

        if (isCustomArt && MusicLibrary.listOfAllImages.size()!=0 && !checkFlag && !isVideoFile ) {


            GlideApp
                    .with(holder.artView.getContext())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .load(new File(randomArt()))
                    .placeholder(R.drawable.default_album)
                    .transition(withCrossFade(factory))
                    .error(R.drawable.default_album)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.artView);

        }else if (isVideoFile) {
                GlideApp
                        .with(holder.artView.getContext())
                        .asBitmap()
                        .load(Uri.fromFile(new File(mItems.get(position).data)))
                        .transition(withCrossFade(factory))
                        .placeholder(R.drawable.default_album)
                        .error(R.drawable.default_album)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.artView);
            } else {

                GlideApp
                        .with(holder.artView.getContext())
                        .asBitmap()
                        .load(mItems.get(position).data)
                        .transition(withCrossFade(factory))
                        .placeholder(R.drawable.default_album)
                        .signature(new ObjectKey(new File(mItems.get(position).data).lastModified()))
                        .error(R.drawable.default_album)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.artView);
            }

        if (scroll==position) {
            holder.vuMeterView.setVisibility(View.VISIBLE);
            holder.vuMeterView.resume(true);
        }else {
            holder.vuMeterView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onItemDismiss(int position) {
        MusicLibrary.queueData.remove(position);
        MusicLibrary.queueArray.remove(position);
        MusicLibrary.queueItems.remove(position);
        notifyItemRemoved(position);
        if(onItemDismissListener!=null) {
            onItemDismissListener.OnItemDismiss(position);

        }
    }
    public void scrollPos(int pos){
        scroll=pos;

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        Collections.swap(MusicLibrary.queueData, fromPosition, toPosition);
        Collections.swap(MusicLibrary.queueArray, fromPosition, toPosition);
        notifyDataSetChanged();
        if(onItemMoveListener!=null) {
            onItemMoveListener.OnItemMove(fromPosition,toPosition);
        }
        //notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position){
        return  mItems.get(position).hashCode();

    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        private final TextView textView;
        private final ImageButton handleView;
        private final ImageView artView;
        private final TextView songMeta;
      private final   VuMeterView vuMeterView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.title);
            handleView = itemView.findViewById(R.id.option_button);
            artView=itemView.findViewById(R.id.album_imageView);
            songMeta=itemView.findViewById(R.id.description);
            vuMeterView=itemView.findViewById(R.id.vumeter);
        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.5f);
            Log.d("OnItemSelected", "onItemSelected:Invoked ");
           // itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            Log.e("onItemClear", "onItemClear: Invoked" );
            //itemView.setBackgroundColor(0);
        }
    }

    @SuppressLint("DefaultLocale")
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
    private static String getSongFormat(String data){

        return  ((data).substring((data.lastIndexOf(".") + 1)).toUpperCase());
    }
    private static String randomArt() {

        int max = MusicLibrary.listOfAllImages.size();
        rand = new Random();
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
}