package com.McDevelopers.sonaplayer.QueueListActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.McDevelopers.sonaplayer.R;

public class MainFragment extends ListFragment {

    public interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    private OnListItemClickListener mItemClickListener;

    public MainFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
            mItemClickListener = (OnListItemClickListener) a;

            Log.d("OnAttachCalled", "onAttach: InFirstBlock");
        }else {
            mItemClickListener = (OnListItemClickListener) context;
            Log.d("OnAttachCalled", "onAttach: InSecondBlock");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

      /*  final String[] items = getResources().getStringArray(R.array.main_items);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);*/
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        mItemClickListener.onListItemClick(position);
    }
}