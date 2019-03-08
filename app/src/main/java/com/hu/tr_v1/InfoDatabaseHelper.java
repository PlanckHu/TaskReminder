package com.hu.tr_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class InfoDatabaseHelper extends SQLiteOpenHelper {

    private final String CreateTaskTable = "create table TaskInfo (" +
            "id integer primary key autoincrement, " +
            "name text not null, " +
            "finished integer default 0,"+
            "begin_time text default 'none', " +
            "deadline text default 'none', " +
            "content text default 'none')";

    private Context mContext;
    public InfoDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTaskTable);
        Toast.makeText(mContext, "Create sql!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists TaskInfo");
        onCreate(db);
    }
}
