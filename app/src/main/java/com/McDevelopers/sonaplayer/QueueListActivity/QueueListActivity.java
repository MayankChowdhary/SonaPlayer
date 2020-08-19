package com.McDevelopers.sonaplayer.QueueListActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.Animations;
import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.MediaListActivity.MediaListActivity;
import com.McDevelopers.sonaplayer.MusicLibrary;
import com.McDevelopers.sonaplayer.QueueListActivity.helper.OnStartDragListener;
import com.McDevelopers.sonaplayer.QueueListActivity.helper.SimpleItemTouchHelperCallback;
import com.McDevelopers.sonaplayer.R;
import com.McDevelopers.sonaplayer.SonaToast;
import com.McDevelopers.sonaplayer.Vibration;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class QueueListActivity extends AppCompatActivity  implements OnStartDragListener {
    private ItemTouchHelper mItemTouchHelper;
    protected static List<QueueItems> mItemsX ;
   private TextView queueTitle;
    private LinearLayout emptyQueueLayout;
    private  RecyclerView recyclerView;
    private ImageButton addQueueButton;
    private ImageButton queueClear;
    SharedPreferences currentState;
    private static int enterAnim=6;
    static   RecyclerListAdapter adapter;
    private  static  boolean saveQueueFlag=false;
    int scrollPosition = 0;
    private VerticalRecyclerViewFastScroller fastScroller;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        mItemsX=MusicLibrary.queueItems;
        Intent i = getIntent();
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        enterAnim=currentState.getInt("enterAnim",6);

        setContentView(R.layout.activity_queue_list);
        queueTitle=findViewById(R.id.queue_header_title);
        emptyQueueLayout=findViewById(R.id.empty_queue_view);
         recyclerView = findViewById(R.id.queue_recycler_view);
        fastScroller = findViewById(R.id.fast_scroller);
        addQueueButton=findViewById(R.id.add_queue);
        queueClear=findViewById(R.id.clear_queue);

        addQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibration.Companion.vibrate(50);
                Intent intent = new Intent(QueueListActivity.this, MediaListActivity.class);
                intent.putExtra("MARK",true );
                intent.putExtra("FOCUS", false);
                intent.putExtra("scrollId", -1);
                startActivity(intent);
                Animations.Animations(QueueListActivity.this,enterAnim);
                finish();
            }
        });

        queueClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibration.Companion.vibrate(50);
                if(MusicLibrary.queueItems.size()==0){
                    SonaToast.setToast(getApplicationContext(),"Queue is Empty!",0);
                }else{

                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(QueueListActivity.this, R.style.myDialog));
                    builder1.setMessage("Clear Queue ?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Clear",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Vibration.Companion.vibrate(50);
                                    Intent intent =new Intent("QueueToggle");
                                    intent.putExtra("StartActivity",false);
                                    intent.putExtra("StartQueue",true);
                                    intent.putExtra("QueueSwitch",false);
                                    intent.putExtra("SkipNext",true);
                                    sendBroadcast(intent);

                                    recyclerView.setAdapter(null);
                                    MusicLibrary.queueItems.clear();
                                    MusicLibrary.queueData.clear();
                                    MusicLibrary.queueArray.clear();
                                    recyclerView.setVisibility(View.GONE);
                                    fastScroller.setEnabled(false);
                                    fastScroller.setVisibility(View.GONE);
                                    emptyQueueLayout.setVisibility(View.VISIBLE);
                                    queueTitle.setText("Queue");
                                    SharedPreferences  currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor;
                                    editor = currentState.edit();
                                    editor.remove("QueueData");
                                    editor.commit();
                                }
                            });

                    builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    if(alert11.getWindow()!=null)
                        alert11.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    alert11.show();

                }
            }
        });

        if(MusicLibrary.queueItems.size()==0){
            recyclerView.setVisibility(View.GONE);
            fastScroller.setEnabled(false);
            fastScroller.setVisibility(View.GONE);
            emptyQueueLayout.setVisibility(View.VISIBLE);
        }else {
        adapter = new RecyclerListAdapter(this, this);
            recyclerView.setHasFixedSize(true);
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            queueTitle.setText("Queue ( " + adapter.getItemCount() + " )");
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
            fastScroller.setRecyclerView(recyclerView);
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());

            adapter.setOnItemClickListener(new RecyclerListAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    Intent intent = new Intent();
                    Log.d("QueueSelected", "OnItemClick: MediaId"+position);
                    intent.putExtra("mediaId",mItemsX.get(position).mediaId);
                    intent.putExtra("queueStatus",true);
                    setResult(RESULT_OK, intent);
                    if(saveQueueFlag){
                        MusicLibrary.saveQueue();
                        Log.d("SaveQueueInvoked", "onBackPressed:QueueListActivity ");
                    }
                    finish();
                }
            });

          adapter.setOnItemDismissListener(new RecyclerListAdapter.OnItemDismissListener() {
              @Override
              public void OnItemDismiss(int position) {
                  saveQueueFlag=true;
                  queueTitle.setText("Queue ( " + adapter.getItemCount() + " )");
                  if(adapter.getItemCount()<=0){
                      Intent intent =new Intent("QueueToggle");
                      intent.putExtra("StartActivity",false);
                      intent.putExtra("StartQueue",true);
                      intent.putExtra("QueueSwitch",false);
                      intent.putExtra("SkipNext",true);
                      sendBroadcast(intent);
                  }
              }
          });

          adapter.setOnItemMoveListener(new RecyclerListAdapter.OnItemMoveListener() {
              @Override
              public void OnItemMove(int fromPosition, int toPosition) {
                 saveQueueFlag=true;
              }
          });
        }

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume(){

        super.onResume();
        restoreScrollPos();
    }
    @Override
    public void onBackPressed() {
    super.onBackPressed();
    if(saveQueueFlag){
        MusicLibrary.saveQueue();
        Log.d("SaveQueueInvoked", "onBackPressed:QueueListActivity ");
    }
    }
    private void restoreScrollPos(){
        SharedPreferences mediaIds = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        String MediaId = mediaIds.getString("searchId", "");
        boolean isQueueActive=mediaIds.getBoolean("QueueFlag",false);
        if(!isQueueActive)
            return;

        int searchListLength = mItemsX.size();
        for (int j = 0; j < searchListLength; j++) {
            if (mItemsX.get(j).mediaId.equals(MediaId)) {
                scrollPosition = j;
                break;
            }
        }
            if(adapter!=null) {
                adapter.scrollPos(scrollPosition);
                recyclerView.scrollToPosition(scrollPosition);
            }

    }
}
