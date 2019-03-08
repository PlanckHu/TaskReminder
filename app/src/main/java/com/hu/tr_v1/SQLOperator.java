package com.hu.tr_v1;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行从数据库的 增删改查的操作
 * input args and their values
 * return SingleTask or List of SingleTask
 */
public class SQLOperator {

    /**
     * TaskInfo
     * id integer primary key autoincrement
     * name text
     * finished integer
     * begin_time text
     * deadline text
     */
    private static final String TAG = "SQLOperator";

    private static InfoDatabaseHelper infoHelper;

    public void setInfoDatabaseHelper(InfoDatabaseHelper infoDatabaseHelper) {
        this.infoHelper = infoDatabaseHelper;
    }

    public static void initData() {
        SQLiteDatabase db = infoHelper.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "This database isn't exist. -- initializing");
        } else {
            Log.d(TAG, "initializing the data");
//            db.execSQL("insert into TaskInfo (name, finished, begin_time, deadline) values(?, ?, ?, ?)",
//                    new String[]{"Introduction", "0", "2019-3-5", "none"});
        }
    }

    /**
     * 在TaskInfo中查询
     *
     * @param query select 语句
     * @param args  对应"where .. =  ?" 中"?"的参数值
     * @return 返回符合参数的从数据库中选择出来的项
     */
    public static List<SingleTask> executeQuery(String query, String[] args) {
        SQLiteDatabase db = infoHelper.getWritableDatabase();
        if (db == null) {
            Log.d(TAG, "This database isn't exist. --executing query");
            return null;
        }
        Log.d(TAG, "reading");
        Cursor cursor = db.rawQuery(query, args);
        if (cursor == null || cursor.getCount() == 0) return null;
        cursor.moveToFirst();
        List<SingleTask> list = new ArrayList<>();
        do {
            Log.d(TAG, "reading on cursor " + cursor.getCount());
            SingleTask singleTask = new SingleTask(cursor.getInt(cursor.getColumnIndex("id")));
            singleTask.setTaskName(cursor.getString(cursor.getColumnIndex("name")));
            int finished = cursor.getInt(cursor.getColumnIndex("finished"));
            singleTask.setFinished(finished == 1);
            singleTask.setTaskTime(cursor.getString(cursor.getColumnIndex("begin_time")));
            singleTask.setTaskContent(cursor.getString(cursor.getColumnIndex("content")));
            list.add(singleTask);
        } while (cursor.moveToNext());
        return list;
    }

    /**
     * 插入表
     *
     * @param paras  数据库中的列名
     * @param values
     */
    public static void insertTask(String[] paras, String[] values) {
        Log.d(TAG, "inserting");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("insert into TaskInfo (");
        for (int i = 0; i < paras.length; i++) {
            stringBuilder.append(paras[i]);
            if (i != paras.length - 1)
                stringBuilder.append(", ");
        }
        stringBuilder.append(") values(");
        for (int i = 0; i < paras.length; i++) {
            stringBuilder.append("?");
            if (i != paras.length - 1)
                stringBuilder.append(", ");
        }
        stringBuilder.append(')');
        Log.d(TAG, "inserting -- string built");
        SQLiteDatabase db = infoHelper.getWritableDatabase();
        if (db != null)
            db.execSQL(stringBuilder.toString(), values);
        Log.d(TAG, "finished inserting");
    }


    public static void deleteDataById(int id) {
        SQLiteDatabase database = infoHelper.getWritableDatabase();
        Integer[] val = new Integer[]{id};
        database.execSQL("delete from TaskInfo where id = ?", val);
    }

    public static void updateDataById(int id, ContentValues contentValues) {
        // 好吧屈服了
        // 还是人家的这个函数写得好一些
        SQLiteDatabase database = infoHelper.getWritableDatabase();
        database.update("TaskInfo", contentValues, "id = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "Update!");
    }
}
