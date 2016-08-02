package com.byku.android.kamstore.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

public class StoreDataSource {
    private static StoreDataSource instance;
    private SQLiteDatabase database;
    private StoreDatabaseHelper dbHelper;
    private String[] allColumns = {StoreDatabaseHelper.COLUMN_ID, StoreDatabaseHelper.COLUMN_NAME,
            StoreDatabaseHelper.COLUMN_DESC, StoreDatabaseHelper.COLUMN_COST};

    private StoreDataSource(Context context){
        dbHelper = new StoreDatabaseHelper(context);
    }

    public static StoreDataSource createStoreDataSource(Context context){
        if(instance == null){
            synchronized (StoreDataSource.class){
                if(instance==null){
                    instance = new StoreDataSource(context);
                }
            }
        }
        return instance;
    }

    public static StoreDataSource getStoreDataSource(){
        return instance;
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }

    public void storeAllItems(ArrayList<Item> items,StoreDataSource dataSource){
        dbHelper.onNewData(database);
        for(Item item : items){
            Log.i("SAVE","Sotring " + item);
            ContentValues values = new ContentValues();
            values.put(StoreDatabaseHelper.COLUMN_NAME,item.getName());
            values.put(StoreDatabaseHelper.COLUMN_DESC, item.getDesc());
            values.put(StoreDatabaseHelper.COLUMN_COST,item.getCost());
            long insertId = database.insert(StoreDatabaseHelper.TABLE_STORE,null,values);
            Cursor cursor = database.query(StoreDatabaseHelper.TABLE_STORE, allColumns,
                    StoreDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            cursor.close();
        }
        dataSource.close();
    }

    public ArrayList<Item> getAllItems(){
        ArrayList<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(StoreDatabaseHelper.TABLE_STORE, allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private Item cursorToItem(Cursor cursor){
        Item item = new Item(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
        return item;
    }
}
