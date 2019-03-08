package com.hu.tr_v1;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SubColumnAdapter extends RecyclerView.Adapter<SubColumnAdapter.ViewHolder> {

    private String TAG = "SubColumnAdapter";
    private List<R.id> SubcolumnIds;
    private MainActivity mainActivity;

    public SubColumnAdapter() {

    }

    public SubColumnAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        Log.d(TAG, "init");
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setSubcolumnIds(List<R.id> subcolumnIds) {
        SubcolumnIds = subcolumnIds;
    }

    public List<R.id> getSubcolumnIds() {
        return SubcolumnIds;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.sub_column_recycler_view);
            Log.d("SubColumnAdapter", "initViewHolder");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sub_column_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        Log.d(TAG, "onCreateViewHolder");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder");
        TaskAdapter taskAdapter = new TaskAdapter();
        // 每个栏目里要存放的数据不一致
        readTask(taskAdapter, i);
        viewHolder.recyclerView.setAdapter(taskAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewHolder.recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void readTask(TaskAdapter taskAdapter, int subColumn){
        // 读取数据库中的内容
        if(subColumn == 0){
            List<SingleTask> list;
            String[] str = new String[1];
            str[0] = String.valueOf(subColumn);
            list = SQLOperator.executeQuery("select * from TaskInfo where finished = ?", str);
            taskAdapter.setTaskList(list);
        }
    }

    @Override
    public int getItemCount() {
        if (SubcolumnIds != null)
            return SubcolumnIds.size();
        else
            return 1;
    }
}
