package com.hu.tr1.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hu.tr1.R;
import com.hu.tr1.ViewPagerOperator;
import com.hu.tr1.Task;

import java.util.List;

//TODO:现在的问题是，不希望ViewPage在切换页面时自动刷新页面

/**
 * checkbox改变
 * 1. 所有页面里该条目的checkbox都会改变
 * 2. 该状态不会反映在数据库里，且暂时不会被分类
 * 3. 按了刷新键之后，该状态才会反映在数据库里且会被分类
 * 综上所述，好像需要一个数据缓存的机制
 * 正常情况下只从该缓存中读取数据，但是
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private static final String TAG = "TaskAdapter";
    private List<Task> taskList;

    public TaskAdapter(List<Task> list) {
        this.taskList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox finishedCheckBox;
        public TextView taskNameTextView;
        public TextView startTimeTextView;
        public TextView endTimeTextView;
        public ImageView operatingDot;
        public ConstraintLayout taskRootLayout;
        public ImageView deleteTaskIcon;
        public ImageView editTaskIcon;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            finishedCheckBox = itemView.findViewById(R.id.task_done_checkbox);
            taskNameTextView = itemView.findViewById(R.id.name_of_task_text_view);
            startTimeTextView = itemView.findViewById(R.id.start_time_text_view);
            endTimeTextView = itemView.findViewById(R.id.end_time_text_view);
            operatingDot = itemView.findViewById(R.id.operate_dot);
            taskRootLayout = itemView.findViewById(R.id.root_task_layout);
            deleteTaskIcon = itemView.findViewById(R.id.delete_task_png);
            editTaskIcon = itemView.findViewById(R.id.edit_task_png);
            // 将原来的layout存起来
            operatingDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 弹出窗口相应的动作
                    ViewPagerOperator.changedTaskLayout(taskRootLayout);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_colunm_layout, viewGroup, false);
        return (new ViewHolder(view));
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Task task = taskList.get(i);
        Log.d(TAG, "setting view " + i + " in recyclerView");
        int finished = task.isFinished();
        viewHolder.finishedCheckBox.setChecked(finished == 1);
        viewHolder.taskNameTextView.setText(task.getTaskName());
        viewHolder.startTimeTextView.setText(task.getStartTime());
        viewHolder.endTimeTextView.setText(task.getEndTime());
        //点击checkbox的事件
        //应该要点击更改后先不更改界面，在点击同步按钮之后才更新
        viewHolder.finishedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v(TAG, "oncheckedchanged");
                ViewPagerOperator.setTaskFinished(task.getNumber(), isChecked);
            }
        });
        //点击删除图标事件
        viewHolder.deleteTaskIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "click on delete icon");
                ViewPagerOperator.removeFromAllTasks(task);
            }
        });
        viewHolder.editTaskIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "click on edit icon");
                ViewPagerOperator.editTaskInfo(task);
            }
        });

        //给每一项设置背景颜色
        if (i % 2 == 1)
            viewHolder.taskRootLayout.setBackgroundColor(Color.rgb(227, 227, 227));//background_light_grey
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


}
