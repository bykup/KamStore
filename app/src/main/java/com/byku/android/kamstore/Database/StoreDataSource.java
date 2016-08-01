package com.byku.android.kamstore.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.byku.android.kamstore.RecView.Abstract.ItemAdapter;
import com.byku.android.kamstore.RecView.Item;

import java.util.ArrayList;

/**
 * Created by Byku on 01.08.2016.
 */
public class StoreDataSource {
    private SQLiteDatabase database;
    private StoreDatabaseHelper dbHelper;
    private String[] allColumns = {StoreDatabaseHelper.COLUMN_ID, StoreDatabaseHelper.COLUMN_NAME,
            StoreDatabaseHelper.COLUMN_DESC, StoreDatabaseHelper.COLUMN_COST};

    public StoreDataSource(Context context){
        dbHelper = new StoreDatabaseHelper(context);
    }
    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }
    public Item createItem(String name, String desc, String cost){
        ContentValues values = new ContentValues();
        values.put(StoreDatabaseHelper.COLUMN_NAME,name);
        long insertId = database.insert(StoreDatabaseHelper.TABLE_STORE,null,values);
        Cursor cursor = database.query(StoreDatabaseHelper.TABLE_STORE, allColumns,
                StoreDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Item newItem = cursorToItem(cursor);
        cursor.close();
        return newItem;
    }
    public void deleteItem(Item item){
        long id = item.getId();
        database.delete(StoreDatabaseHelper.TABLE_STORE,StoreDatabaseHelper.COLUMN_ID + " = " + id, null);
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
        Item item = new Item(cursor.getLong(0),cursor.getString(1),"test",300);
        return item;
    }
}
