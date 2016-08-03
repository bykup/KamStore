package com.byku.android.kamstore.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_STORE = "shop";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_COST = "cost";

    public static final String DATABASE_NAME = "store.db";
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_STORE + "(" + COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_NAME + " text not null, " + COLUMN_DESC +
            " text, " + COLUMN_COST + " text);";

    public StoreDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * Destroys current database and creates it anew.
     * I came to that decision, as we won't be saving to database frequently(only once), so it was pointless to
     * create some kind of "sophisticated" synchronization algorithm
     * @param db - database which we want to reinitialize with new data
     */
    public void onNewData(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
        onCreate(db);
    }
}
