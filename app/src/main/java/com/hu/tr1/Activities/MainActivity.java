package com.hu.tr1.Activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hu.tr1.DataCache;
import com.hu.tr1.R;
import com.hu.tr1.ViewPagerOperator;
import com.hu.tr1.SqliteOperator;
import com.hu.tr1.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final int infoEditActivityRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // actionbar 设置
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.v(TAG, "set");


        //设置界面上的ViewPager
        Log.v(TAG, "setting viewpager");
        ViewPagerOperator.setMainActivity(this);
        ViewPagerOperator.initViewPager();

        //先手动放置些测试数据
//        List<Task> tasks = new ArrayList<>();
//        testTasks(tasks);

        // 数据库初始化
        Log.v(TAG, "setting sql");
        SqliteOperator sqliteOperator = SqliteOperator.getInstance();
        sqliteOperator.init(this);
        Log.v(TAG, "done setting");
    }

    public void OnClickAllButton(View view) {
        ViewPager pager = findViewById(R.id.view_pager);
        if (pager == null) {
            Toast.makeText(this, "click all btn -- null", Toast.LENGTH_SHORT).show();
            return;
        }
        //修改当前页为 0
        pager.setCurrentItem(0, true);
//        btnHintAnimation(view.getId());
    }

    //TODO:为啥点击切换页面不是顺滑的切换
    public void OnClickUndoneButton(View view) {
        ViewPager pager = findViewById(R.id.view_pager);
        if (pager == null) return;
        pager.setCurrentItem(1, true);
    }

    public void OnClickDoneButton(View view) {
        ViewPager pager = findViewById(R.id.view_pager);
        if (pager == null) return;
        pager.setCurrentItem(2, true);
    }

    /**
     * 分类栏下的引导横线动画
     *
     * @param id
     */
    public void btnHintAnimation(int id) {
        TextView btnHint = findViewById(R.id.classifying_btn_hint);
        Button target = findViewById(id);
        ConstraintLayout constraintLayout = findViewById(R.id.main_activity_root_layout);
        TransitionManager.beginDelayedTransition(constraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(btnHint.getId(), ConstraintSet.START, target.getId(), ConstraintSet.START);
        constraintSet.connect(btnHint.getId(), ConstraintSet.END, target.getId(), ConstraintSet.END);
        constraintSet.applyTo(constraintLayout);
    }

//    public static void testTasks(List<Task> list) {
//        for (int i = 0; i < 4; i++) {
//            Task task = new Task();
//            task.setTaskName("name " + Integer.toString(i));
//            task.setFinished(i / 2 == 0);
//            task.setStartTime("2018." + Integer.toString(i));
//            task.setEndTime("2019." + Integer.toString(i));
//            list.add(task);
//        }
//    }

    // set action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * backup_data: 包括Task的同步和界面的更新
     * TODO: 点击backup的按钮之后加入“加载中”的转动的圈圈
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup_data:
//                ViewPagerOperator.setDataChanged(true);
                DataCache dataCache = DataCache.getInstance();
                dataCache.updateSQLite();
                Toast.makeText(this, "update sql", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_item:
                Toast.makeText(this, "add Item", Toast.LENGTH_SHORT).show();
                createNewTask();
                break;
            default:
                break;
        }
        return true;
    }

    public void createNewTask(Task task) {
        Intent intent = new Intent(MainActivity.this, InfoEditActivity.class);
        if (task != null) {
            intent.putExtra("newly", false);
            intent.putExtra("id", task.getNumber());
            intent.putExtra("name", task.getTaskName());
            intent.putExtra("start_time", task.getStartTime());
            intent.putExtra("end_time", task.getEndTime());
            intent.putExtra("contents", task.getContents());
        } else {
            intent.putExtra("newly", true);
        }
        startActivityForResult(intent, infoEditActivityRequestCode);
    }

    public void createNewTask() {
        createNewTask(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case infoEditActivityRequestCode:
                backFromInfoEditActivity(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void backFromInfoEditActivity(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            Task task = new Task();
            task.setFinished(false);
            task.setTaskName(intent.getStringExtra("conclusion"));
            task.setStartTime(intent.getStringExtra("start_time"));
            task.setEndTime(intent.getStringExtra("end_time"));
            task.setContents(intent.getStringExtra("content"));
            DataCache dataCache = DataCache.getInstance();
            int id = intent.getIntExtra("id", -1);
            if (id == -1)
                dataCache.append(task);
            else {
                Log.v(TAG, "revise a task");
                task.setNumber(id);
                dataCache.reviseTask(task);
            }
            ViewPagerOperator.notifyDataChanged();
            Toast.makeText(getApplicationContext(), "Confirm", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
        }
    }
}
