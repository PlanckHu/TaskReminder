package com.hu.tr1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SqliteOperator {
    private final String TAG = "SqliteOperator";
    private static final SqliteOperator ourInstance = new SqliteOperator();

    public static SqliteOperator getInstance() {
        return ourInstance;
    }

    private SqliteOperator() {
    }

    private InfoDatabaseHelper infoHelper = null;
    private SQLiteDatabase database = null;

    public void init(Context context) {
        if (infoHelper == null) {
            infoHelper = new InfoDatabaseHelper(context, "TaskInfo.db", null, 1);
            database = infoHelper.getWritableDatabase();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void insert(List<Task> tasks) {
        for (Task task : tasks) {
            insert(task);
        }
    }

    // 插入
    public void insert(Task task) {
        insert(task.getParaNames(), task.getParaVals());
    }

    public void insert(String[] paras, String[] values) {
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

    public List<Task> selectAll() {
        return select("*", null);
    }

    public List<Task> select(String condition) {
        return select("*", condition);
    }

    /**
     * 检索
     * @param neededVariable  name of the needed variable. "*" means all.
     * @param conditionString example: condition = " finish=0 "
     * @return List of tasks that meet the requirements.
     */
    public List<Task> select(String neededVariable, String conditionString) {
        List<Task> tasks = new ArrayList<>();
        Cursor cursor = null;
        if (conditionString == null)
            cursor = database.rawQuery("select * from TaskInfo", null);
        else {
            StringBuilder stringBuilder = new StringBuilder();
            String[] con = new String[1];
            // 分割 "finished = 0” 为 “finished = ?" 和 "0"
            String[] strings = separateConditionString(conditionString);
            stringBuilder.append("select ").append(neededVariable)
                    .append(" from TaskInfo where ").append(strings[0]);
            con[0] = strings[1];
            //读取
            if (infoHelper == null) {
                Log.v(TAG, "infohelper = null");
                return null;
            }
            cursor = database.rawQuery(stringBuilder.toString(), con);
        }
        if (cursor == null || cursor.getCount() == 0) {
            if (cursor == null)
                Log.v(TAG, "cursor = null");
            else if (cursor.getCount() == 0)
                Log.d(TAG, "cursor.getCount() = 0");
            return null;
        }
        cursor.moveToFirst();
        Log.d(TAG, "reading on cursor " + cursor.getCount());
        do {
            Task task = new Task();
            task.setNumber(cursor.getInt(cursor.getColumnIndex("id")));
            task.setTaskName(cursor.getString(cursor.getColumnIndex("name")));
            task.setStartTime(cursor.getString(cursor.getColumnIndex("start_time")));
            task.setEndTime(cursor.getString(cursor.getColumnIndex("end_time")));
            int finished = cursor.getInt(cursor.getColumnIndex("finished"));
            task.setFinished(finished == 1);
            task.setContents(cursor.getString(cursor.getColumnIndex("contents")));
            tasks.add(task);
        } while (cursor.moveToNext());
        cursor.close();
        return tasks;

    }

    private String[] separateConditionString(String conditionString) {
//        Log.d(TAG, "separating");
        String[] strings = new String[2];
        StringBuilder builder = new StringBuilder(conditionString);
        int index = builder.indexOf("=");
        strings[1] = builder.substring(index + 1, builder.length());
//        Log.d(TAG, strings[1]);
        strings[0] = builder.delete(index + 1, builder.length()).append("?").toString();
//        Log.d(TAG, strings[0]);
        return strings;
    }

    /**
     * OK
     * 从数据库删除
     */
    public void delete(Task task) {
        delete(task.getNumber());
    }

    public void delete(int id) {
        if (database == null)
            database = infoHelper.getWritableDatabase();
        Integer[] val = new Integer[]{id};
        database.execSQL("delete from TaskInfo where id = ?", val);
        Log.v(TAG, "delete task(id=" + id + ")");
    }

    /**
     * 更改数据
     * TODO: 1. 写一个 update(Task task),就直接跟数据库更新
     */
    public void update(Task task) {
        ContentValues contentValues = new ContentValues();
        String[] paraNames = task.getParaNames();
        String[] paraValues = task.getParaVals();
        for (int i = 0; i < paraNames.length; i++) {
            if (paraNames[i].equals("id"))
                break;
            contentValues.put(paraNames[i], paraValues[i]);
        }
        update(task, contentValues);
    }

    public void update(Task task, ContentValues contentValues) {
        if (database == null)
            database = infoHelper.getWritableDatabase();
        database.update("TaskInfo", contentValues, "id=?", new String[]{String.valueOf(task.getNumber())});
        Log.v(TAG, "update task(id=" + task.getNumber() + ")");
    }

    public void update(Task task, boolean checked) {
        if (database == null)
            database = infoHelper.getWritableDatabase();
        StringBuilder builder = new StringBuilder("update TaskInfo Set finished = ");
        if (checked)
            builder.append(1);
        else
            builder.append(0);
        builder.append(" where id = " + task.getNumber());
        Log.v(TAG, builder.toString());
        database.execSQL(builder.toString());
//        database.execSQL("update TaskInfo Set finished = 1 where id = " + task.getNumber());
    }
}
