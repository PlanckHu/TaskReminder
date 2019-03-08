package com.hu.tr_v1;

import android.content.ContentValues;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class InfoPanel extends ConstraintLayout {

    private final int[] editTextIds = {R.id.info_panel_task_name,
            R.id.info_panel_task_begin_time,
            R.id.info_panel_deadline,
            R.id.info_panel_task_content};
    private final String TAG = "InfoPanel";

    public InfoPanel(Context context) {
        super(context);
        init(context);
    }

    public InfoPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InfoPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.info_panel_view, this);
    }

    public void setInit(final SingleTask task) {
        String[] args = new String[]{task.getTaskName(),
                task.getTaskTime(),
                task.getDeadline(),
                task.getTaskContent()};

        for (int i = 0; i < 4; i++) {
            EditText editText = findViewById(editTextIds[i]);
            editText.setText(args[i]);
        }

        /**
         * 按下确定会发生：
         *  1. 退出infoPanel
         *  2. 修改数据库数据
         *  3. 界面数据更新
         */
        ImageView infoPanelConfirm = findViewById(R.id.info_panel_confirm);
        infoPanelConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click on infoPanel confirm");
                // 退出infoPanel
                AnimationOperator.infoPanelOut();
                // 修改数据库数据
                ContentValues vals = getValues();
                SQLOperator.updateDataById(task.getId(), vals);
                // 更新数据
                BroadcastOperator.broadcastRenewing();
            }
        });

        /**
         * 按下cancel，当无事发生过
         */
        ImageView infoPanelCancel = findViewById(R.id.info_panel_cancel);
        infoPanelCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click on infoPanel cancel");
                AnimationOperator.infoPanelOut();
            }
        });

    }

    private ContentValues getValues() {
        ContentValues values = new ContentValues();
        String[] paras = new String[]{"name", "begin_time", "deadline", "content"};
        for (int i = 0; i < 4; i++) {
            EditText editText = findViewById(editTextIds[i]);
            values.put(paras[i], editText.getText().toString());
        }
        return values;
    }
}
