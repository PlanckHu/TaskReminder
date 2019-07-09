package com.hu.tr1;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hu.tr1.Activities.MainActivity;
import com.hu.tr1.Adapter.TaskAdapter;
import com.hu.tr1.Adapter.ViewAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO:用单例模式和静态
public class ViewPagerOperator {

    private static final int pageNum = 3;
    private static final String TAG = "ViewPagerOperator";
    private static ConstraintLayout changedLayout = null;
    private static MainActivity main;
    private static boolean[] pageRenewed = new boolean[3];

    public static void setMainActivity(MainActivity mainActivity) {
        main = mainActivity;
    }

    public static void initViewPager() {
        initViewPager(0);
    }

    // TODO: 怎么更新数据！
    private static void initViewPager(int position) {
        final ViewPager viewPager = main.findViewById(R.id.view_pager);
        viewPager.setAdapter(initViewPageAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                int id = 0;
                if (i == 0) {
                    id = R.id.all_title_btn;
                } else if (i == 1) {
                    id = R.id.undone_title_btn;
                } else if (i == 2) {
                    id = R.id.done_title_btn;
                }
                main.btnHintAnimation(id);
                Log.v(TAG, "page " + i + " selected");

            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        // 下面这段是用反射的方法将adapter的默认初始值改变为position
        // 太强了orz...
        try {
            Class c = Class.forName("android.support.v4.view.ViewPager");
            Field field = c.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(viewPager, position);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setPageRenewed(boolean renewed, int position) {
        pageRenewed[position] = renewed;
    }

    public static boolean getPageRenewed(int position) {
        return pageRenewed[position];
    }

    private static ViewAdapter initViewPageAdapter() {
        List<View> pages = new ArrayList<>();
        inputDataToClassifyingPages(pages);
        return new ViewAdapter(pages);
    }

    private static void inputDataToClassifyingPages(List<View> pages) {
        //添加页面
        LayoutInflater inflater = main.getLayoutInflater();
        View view1 = inflater.inflate(R.layout.layout1, null);
        View view2 = inflater.inflate(R.layout.layout1, null);
        View view3 = inflater.inflate(R.layout.layout1, null);
        pages.add(view1);
        pages.add(view2);
        pages.add(view3);
    }

    public static void removeFromAllTasks(Task task) {
        DataCache dataCache = DataCache.getInstance();
        dataCache.putinBasket(task);
        ViewPager viewPager = main.findViewById(R.id.view_pager);
        initViewPager(viewPager.getCurrentItem());
    }


    public static void initRecyclerView(View view, int pageId) {
        RecyclerView recyclerView = view.findViewById(R.id.layout1_recycler_view);
        setRecyclerViewAdapter(recyclerView, pageId);
        Log.d(TAG, "list got");
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    private static void setRecyclerViewAdapter(RecyclerView recyclerView, int pageId) {
        TaskAdapter adapter = null;
        DataCache dataCache = DataCache.getInstance();
        List<Task> tasks = null;
        if (pageId == 0) {
            tasks = dataCache.getDisplayableTask();
        } else if (pageId == 1) {
            tasks = dataCache.getTaskFinished(false);
        } else if (pageId == 2) {
            tasks = dataCache.getTaskFinished(true);
        }
        adapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 指定更改某个task的finish
     *
     * @param id       该task的id
     * @param finished 是否完成（checkbox是否打勾
     *                 list里存的是引用的话，是不是就是我只要改变一次就好的意思
     *                 哦还要分类
     */
    public static void setTaskFinished(int id, boolean finished) {
        DataCache dataCache = DataCache.getInstance();
        dataCache.reviseFinished(id, finished);
        notifyDataChanged();
    }

    public static void notifyDataChanged(){
        for (int i = 0; i < 3; i++) {
            setPageRenewed(false, i);
        }
        ViewPager viewPager = main.findViewById(R.id.view_pager);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public static void setChangedLayout(ConstraintLayout layout) {
        changedLayout = layout;
    }

    // 下面是关于 页面上【（编辑/删除）项】 的弹出和收回
    public static void changedTaskLayout(ConstraintLayout newlyChanged) {
        if (newlyChanged == null) return;

        if (changedLayout == null) {
            popupFuncConstraintLayout(newlyChanged);
            Log.v(TAG, "没有已经弹出小窗");
        } else if (newlyChanged != changedLayout) {
            popupFuncConstraintLayout(newlyChanged);
            resetFuncConstraintLayout(changedLayout);
            Log.v(TAG, "已经有过弹出小窗");
        } else {
            resetFuncConstraintLayout(newlyChanged);
            newlyChanged = null;
            Log.v(TAG, "收回小窗");
        }
        setChangedLayout(newlyChanged);
    }

    private static void popupFuncConstraintLayout(ConstraintLayout taskRootLayout) {
        TransitionManager.beginDelayedTransition(taskRootLayout);
        ConstraintLayout layout = taskRootLayout.findViewById(R.id.func_constrain_layout);
        TextView startTimeHint = taskRootLayout.findViewById(R.id.start_time_text_view);
        TextView endTimeHint = taskRootLayout.findViewById(R.id.end_time_text_view);

        ConstraintSet set = new ConstraintSet();
        set.clone(taskRootLayout);
        set.connect(layout.getId(), ConstraintSet.START, startTimeHint.getId(), ConstraintSet.START);
        set.connect(layout.getId(), ConstraintSet.END, endTimeHint.getId(), ConstraintSet.END);
        set.applyTo(taskRootLayout);
    }

    private static void resetFuncConstraintLayout(ConstraintLayout taskRootLayout) {
        TransitionManager.beginDelayedTransition(taskRootLayout);
        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layout = taskRootLayout.findViewById(R.id.func_constrain_layout);
        ImageView dot = taskRootLayout.findViewById(R.id.operate_dot);

        set.clone(taskRootLayout);
        set.connect(layout.getId(), ConstraintSet.START, dot.getId(), ConstraintSet.END);
        set.connect(layout.getId(), ConstraintSet.END, dot.getId(), ConstraintSet.END);
        set.applyTo(taskRootLayout);
    }

    //下面是关于 页面上点击【编辑】的动作
    public static void editTaskInfo(Task task){
        main.createNewTask(task);
    }
}
