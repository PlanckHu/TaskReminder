package com.hu.tr_v1;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    // taskList 可能接收到null，需要对null的情况做点处理，不然会crash
    private List<SingleTask> taskList;
    private final String TAG = "TaskAdapter";

    public TaskAdapter(){

    }

    public void setTaskList(List<SingleTask> taskList) {
        this.taskList = taskList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView taskName;
        TextView taskTime;
        TextView taskContent;
        ImageView taskTrash;
        ImageView updateBoard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            taskContent = itemView.findViewById(R.id.task_content);
            taskTime = itemView.findViewById(R.id.task_time);
            taskTrash = itemView.findViewById(R.id.delete_trash);
            updateBoard = itemView.findViewById(R.id.update_board);
        }
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_task_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder viewHolder, final int i) {
        if (taskList == null)
            return;
        final SingleTask task = taskList.get(i);
        viewHolder.taskTime.setText(task.getTaskTime());
        viewHolder.taskName.setText(task.getTaskName());
        viewHolder.taskContent.setText(task.getTaskContent());
        /**
         * 从数据库中删除SingleTask
         * 然后更新
         */
        viewHolder.taskTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = task.getId();
                String name = task.getTaskName();
                Log.d(TAG, "id = " + id);
                Log.d(TAG, "name = " + name);
                SQLOperator.deleteDataById(id);
                BroadcastOperator.broadcastRenewing();
            }
        });
        /**
         *  update:
         *  TODO: 弹出一个窗口,将当前条幅的所有信息打出，然后按修改之后返回
         */
        viewHolder.updateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("name", "嗨呀");
//                contentValues.put("begin_time", "2019-2-2");
//                contentValues.put("content", "nothing");
//                SQLOperator.updateDataById(task.getId(),contentValues);
//                Log.d(TAG, "updating");
//                BroadcastOperator.broadcastRenewing();
                AnimationOperator.infoPanelIn(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (taskList == null)
            return 0;
        return taskList.size();
    }
}
