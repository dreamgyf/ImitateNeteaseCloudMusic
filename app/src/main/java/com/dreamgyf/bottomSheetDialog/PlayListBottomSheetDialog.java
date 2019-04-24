package com.dreamgyf.bottomSheetDialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.PlayListStaticAdapter;

public class PlayListBottomSheetDialog extends BottomSheetDialog {

    private RecyclerView recyclerView;

    private View view;

    private Context context;

    public PlayListBottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.bottomsheetdialog_music_list,null,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.getLayoutParams().height = context.getResources().getDisplayMetrics().heightPixels / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(PlayListStaticAdapter.get());
        setContentView(view);
//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
//        bottomSheetBehavior.setPeekHeight(1000);
    }
}
