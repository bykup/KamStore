package com.byku.android.kamstore.database;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

public class DatabaseService extends IntentService {
    /**
     * ifRunning - used to get information if service is currently running(duh)
     * {@link com.byku.android.kamstore.MainActivity}
     * Checked in onOptionsItemSelected
     */
    private static boolean ifRunning = false;
    public static final String DATABASE = "storedatabase";
    public static final String FINISH = "finished";
    public static final String NOTIFICATION = "com.byku.android.kamstore.database";

    private StoreDataSource dataSource;

    public DatabaseService(){ super("DatabaseSaver"); }

    public static boolean getIfRunning(){ return ifRunning; }

    @Override
    protected void onHandleIntent(Intent intent){
        ifRunning = true;
        ArrayList<Item> items = intent.getParcelableArrayListExtra(DatabaseService.DATABASE);
        try {
            dataSource = StoreDataSource.getStoreDataSource();
        }catch(NullPointerException e){
            e.printStackTrace();
            result(Activity.RESULT_CANCELED);
        }finally {
            if(dataSource!=null){
                dataSource.open();
                dataSource.storeAllItems(items,dataSource);
                result(Activity.RESULT_OK);
            }
        }
    }

    private void result(int result){
        ifRunning = false;
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FINISH,result);
        sendBroadcast(intent);
    }
}
