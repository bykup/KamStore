package com.byku.android.kamstore.database;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

public class DatabaseService extends IntentService {
    public static boolean ifRunning = false;
    public static final String DATABASE = "storedatabase";
    public static final String FINISH = "finished";
    public static final String NOTIFICATION = "com.byku.android.kamstore.database";

    private StoreDataSource dataSource;

    public DatabaseService(){
        super("DatabaseSaver");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.i("LOG","Service started");
        ifRunning = true;
        ArrayList<Item> items = intent.getParcelableArrayListExtra(DatabaseService.DATABASE);
        dataSource = StoreDataSource.getStoreDataSource();
        if(dataSource==null){
            result(Activity.RESULT_CANCELED);
        }else {
            dataSource.open();
            dataSource.storeAllItems(items,dataSource);
            result(Activity.RESULT_OK);
        }//*/
    }

    private void result(int result){
        ifRunning = false;
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FINISH,result);
        sendBroadcast(intent);
    }

}