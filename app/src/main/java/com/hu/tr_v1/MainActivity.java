package com.hu.tr_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

//    private LocalBroadcastManager localBroadcastManager;
//    private LocalReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView view = findViewById(R.id.recycler_view_main);
        SubColumnAdapter subColumnAdapter = new SubColumnAdapter(this);
        view.setAdapter(subColumnAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view.setLayoutManager(linearLayoutManager);

        bindBroadcast();
        initSQLite();
        initAnimationOperator();
    }

    /**
     * 之前觉得好像应该用广播
     * 不过现在发现并不需要
     * 先留着这个本地广播看看之后需不需要吧
     */
    private void bindBroadcast(){
        // 绑定本地广播
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        LocalReceiver localReceiver = new LocalReceiver(this);
        BroadcastOperator BroadcastOperator = new BroadcastOperator();
        BroadcastOperator.initBroadcastManager(localBroadcastManager);
        BroadcastOperator.bindForRenewingData(localReceiver);

    }

    /**
     * 初始化数据库
     */
    private void initSQLite(){
        InfoDatabaseHelper infoDatabaseHelper = new InfoDatabaseHelper(this, "TaskInfo.db", null, 2);
        SQLOperator sqlOperator = new SQLOperator();
        sqlOperator.setInfoDatabaseHelper(infoDatabaseHelper);
        SQLOperator.initData();
    }

    /**
     * 初始化AnimationOperator
     */
    private void initAnimationOperator(){
        InfoPanel infoPanel = findViewById(R.id.main_activity_info_panel);
        AnimationOperator operator = new AnimationOperator();
        operator.setInfoPanel(infoPanel);
    }

    public void onClickTaskAdding(View view) {
        // 跳转至TaskAddingActivity，但是不要销毁现在这个activity
        Intent intent = new Intent(this, TaskAdding.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        localBroadcastManager.unregisterReceiver(localReceiver);
        BroadcastOperator.onDestory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "get result from OK");
                    BroadcastOperator.broadcastRenewing();
                    Log.d(TAG, "on Activity Result");
                    renewData();
                }
                else if (resultCode == RESULT_CANCELED)
                    Log.d(TAG, "get result from CANCEL");
                break;
            default:
                break;
        }
    }

    /**
     * 对 recycler_view_main 中的数据进行更新
     * 应该在添加/删除/更新 数据库之后执行
     */
    public void renewData(){
        Log.d(TAG, "renewing data");
        RecyclerView recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

}
