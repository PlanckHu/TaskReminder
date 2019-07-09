package com.hu.tr1;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;

import java.security.Key;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: 检验比较一下之前的频繁与SQL交互的方法 和 现在这种缓存的方法所耗时间和内存占用等的不同
 * 做成单例模式的DataCache
 * 是一个自定的数据缓存
 * 记录数据修改
 * <p>
 * 所有在用户界面上的修改都不会直接反映在数据库上，界面上的分类只能靠"renew"来刷新
 * <p>
 * 增加条目:
 * 1. 所增加的条目添加在页面上，为undone
 * 2. 将增加条目记录在Cache里
 * <p>
 * 修改条目:
 * 1.将条目的修改后的信息放映在页面上
 * 2.将修改记录在Cache里
 * <p>
 * 删除条目:
 * 1. 将删除后的信息放映在页面上
 * 2. 将删除记录在Cache里
 */
public class DataCache {
    private final String TAG = "DataCache";
    private static DataCache instance = null;
    private int maxId = 0;

    private DataCache() {
    }

    public static DataCache getInstance() {
        if (instance == null)
            instance = new DataCache();
        return instance;
    }

    /**
     * revisionMap 是个修改表,记录被改的 Task
     * Key类型是Task id，对应的是原始数据，也就是从数据库中取出来的数据
     * Value类型也是Task，对应的是修改后的数据，也就是在用户界面中修改后的结果
     */
    public Map<Integer, Task> revisionMap = null;
    /**
     * appendLIst 用于储存新增的List
     */
    public List<Task> appendList = null;
    private List<Integer> basket = null;
    private List<Task> allTask = null;

    private void initialize() {
        Log.v(TAG, "initializing");
        setAllTask();
        appendList = null;
        basket = null;
        revisionMap = null;
    }

    /**
     * 只在初始化时和数据库同步时重新设置allTask
     */
    private void setAllTask() {
        SqliteOperator sqliteOperator = SqliteOperator.getInstance();
        Log.v(TAG, "getting tasks");
        allTask = sqliteOperator.selectAll();
        if (allTask == null)
            Log.v(TAG, "get no tasks");
    }

    public List<Task> getAllTask() {
//        Log.v(TAG, "getAllTask");
        if (allTask == null)
            setAllTask();
        List<Task> temp = new ArrayList<>();
        if (allTask != null)
            temp.addAll(allTask);
        if (appendList != null)
            temp.addAll(appendList);
        if (revisionMap != null) {
//            Log.v(TAG, "revisionMap != null");
            for (Task task : temp) {
                if (revisionMap.containsKey(task.getNumber())) {
                    task.replace(revisionMap.get(task.getNumber()));
                }
            }
        }
        return temp;
    }

    public List<Task> getDisplayableTask() {
//        Log.v(TAG, "getDisplayableTask");
        List<Task> temp = getAllTask();
        if (basket == null)
            return temp;

        List<Task> delete = new ArrayList<>();
        for (Task task : temp) {
            if (basket.contains(task.getNumber())) {
                delete.add(task);
            }
        }
        temp.removeAll(delete);
//        printDisplayable(temp);
        return temp;
    }

    private void printDisplayable(List<Task> list) {
        for (Task task : list) {
            Log.v(TAG, "task.num=" + task.getNumber() + " | task.name=" + task.getTaskName());
        }
    }

    public List<Task> getTaskFinished(boolean finished) {
        List<Task> tempAll = getDisplayableTask();
        List<Task> request = new ArrayList<>();
        for (Task task : tempAll) {
            if (task.Finished() == finished)
                request.add(task);
        }
        return request;
    }

    public void reviseFinished(int id, boolean finished) {
        List<Task> allTask = getAllTask();
        for (Task task : allTask) {
            if (task.getNumber() == id) {
                Task temp = new Task(task);
                temp.setFinished(finished);
                Log.v(TAG, "revise: id = " + id + " | finished = " + temp.isFinished());
                if (revisionMap == null) {
                    revisionMap = new HashMap<>();
                }
                if (revisionMap.containsKey(id))
                    revisionMap.replace(id, temp);
                else
                    revisionMap.put(id, temp);
                return;
            }
        }
    }

    public void reviseTask(Task task) {
        List<Task> allTask = getAllTask();
        int id = task.getNumber();
        for (Task tempTask : allTask) {
            if (tempTask.getNumber() == id) {
                Log.v(TAG, "revise: id = " + id);
                if (revisionMap == null)
                    revisionMap = new HashMap<>();
                if (revisionMap.containsKey(id))
                    revisionMap.replace(id, task);
                else
                    revisionMap.put(id, task);
                return;
            }
        }
    }

    /**
     * 往Cache里添加新的task
     *
     * @param task
     */
    public void append(Task task) {
        //给一个可用的编号
        List<Task> all = getAllTask();
        if (maxId == 0) {
            for (Task temp : all) {
                maxId = (temp.getNumber() > maxId) ? temp.getNumber() : maxId;
            }
        }
        // 使编号永远不重复，即使是已经被删除的编号也不占用，【因为编号相同的话与数据库同步时会有很多操作不方便
        //TODO: 找到一个绝对不复用编号的方法
        maxId++;
        task.setNumber(maxId);
        //开始添加
        if (appendList == null)
            appendList = new ArrayList<>();
        appendList.add(task);
        Log.v(TAG, "Appends " + task.getNumber());
    }

    /**
     * 将条目放进垃圾桶里
     */
    public void putinBasket(Task task) {
        if (basket == null) {
            basket = new ArrayList<>();
        }
        basket.add(task.getNumber());
    }

    /**
     * 依据所记录的修改内容，对Sqlite进行修改
     * 按照 添加-修改-删除-更新缓存 的顺序
     */
    public void updateSQLite() {
        Log.v(TAG, "update SQL!");
        SqliteOperator sqliteOperator = SqliteOperator.getInstance();

        // 添加条目  done
        if (appendList != null) {
            for (Task task : appendList) {
                Log.v(TAG, "append id=" + task.getNumber() + " | name= " + task.getTaskName());
                sqliteOperator.insert(task);
            }
        }

        // 修改条目 done
        if (revisionMap != null) {
            Log.v(TAG, "revisionMap != null");
            List<Task> revisionList = new ArrayList<>();
            Set<Integer> keySet = revisionMap.keySet();
            for (Integer key : keySet) {
                revisionList.add(revisionMap.get(key));
            }
//            Log.v(TAG, "get revisionList! size = " + revisionList.size());
            for (Task task : revisionList) {
                Log.v(TAG, "revision id=" + task.getNumber());
                //
                sqliteOperator.update(task);
            }
        }


        //删除条目
        if (basket != null) {
            Log.v(TAG, "cleaning the basket!");
            for (int id : basket) {
                sqliteOperator.delete(id);
            }
        }

        // 更新缓存和清除所有表
        initialize();
    }

}
