package com.byku.android.kamstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.byku.android.kamstore.algorithms.*;
import com.byku.android.kamstore.database.DatabaseService;
import com.byku.android.kamstore.database.StoreDataSource;
import com.byku.android.kamstore.recview.*;
import com.byku.android.kamstore.recview.adapters.BasketAdapter;
import com.byku.android.kamstore.recview.adapters.ItemAdapter;
import com.byku.android.kamstore.recview.adapters.ShopAdapter;

public class MainActivity extends AppCompatActivity{
    private StoreDataSource dataSource;

    ItemArrayGenerator iArratGeneerator;

    private RecyclerView recViewShop;
    private RecyclerView recViewBasket;
    private RelativeLayout basketButton;
    private EditText inputSearch;
    private TextView basketStatus;
    private TextView basketQuantity;

    private ItemAdapter shopAdapter;
    private ItemAdapter basketAdapter;
    private ArrayList<Item> itemsShop;
    private ArrayList<Item> itemsBasket = new ArrayList<>();

    /**
     * Used to getExtras from service running in the background that saved the
     * database to internal memory
     * @link DatabaseService
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                    int resultCode = bundle.getInt(DatabaseService.FINISH);
                    if(resultCode == RESULT_OK) {
                        Toast.makeText(MainActivity.this, "Baza zapisana.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(MainActivity.this, "Błąd w zapisie bazy.", Toast.LENGTH_SHORT).show();
                    }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recViewShop = (RecyclerView) findViewById(R.id.store_list);
        recViewBasket = (RecyclerView) findViewById(R.id.basket_list);
        basketButton = (RelativeLayout) findViewById(R.id.basket_button);
        inputSearch = (EditText) findViewById(R.id.search_bar);
        basketStatus = (TextView) findViewById(R.id.basket_status);
        basketQuantity = (TextView) findViewById(R.id.basket_quantity);

        dataSource = StoreDataSource.createStoreDataSource(this);
        boolean ifDatabaseOpen = false;
        try {
            dataSource.open();
            itemsShop = dataSource.getAllItems();
            dataSource.close();
            ifDatabaseOpen = true;
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            if(!ifDatabaseOpen) {
                itemsShop = new ArrayList<Item>();
                Toast.makeText(MainActivity.this, "Błąd wczytania bazy danych!", Toast.LENGTH_LONG).show();
            }
        }

        shopAdapter = new ShopAdapter(this, itemsShop);
        recViewShop.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recViewShop.setItemAnimator(new DefaultItemAnimator());
        recViewShop.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recViewShop.setAdapter(shopAdapter);
        recViewShop.setHasFixedSize(true);


        basketAdapter = new BasketAdapter(this, itemsBasket);
        recViewBasket.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recViewBasket.setItemAnimator(new DefaultItemAnimator());
        recViewBasket.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recViewBasket.setAdapter(basketAdapter);
        recViewBasket.setHasFixedSize(true);

        recViewShop.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewShop, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(!shopAdapter.getIfRemoving() && position >= 0) {
                    shopAdapter.setIfRemoving(true);
                    basketAdapter.addItemSorted(shopAdapter.getItemAtPos(position), itemsBasket);
                    itemsShop.remove(itemsShop.indexOf(shopAdapter.getItemAtPos(position)));
                    shopAdapter.removeItemAnimated(view, position);
                    basketQuantity.setText("" + itemsBasket.size());
                    basketStatus.setText(String.format("%.2f",basketAdapter.getTotalCost())+" zł");
                    if (AnimationAlgorithms.getIfCollapsed(basketButton.getId()) == 1)
                        AnimationAlgorithms.expand(basketButton);//*/
                }
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        basketAdapter.setOnItemClickListener(new BasketAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position){
                if(!basketAdapter.getIfRemoving() && position >= 0) {
                    view.setClickable(false);
                    basketAdapter.setIfRemoving(true);
                    shopAdapter.addItemSorted(basketAdapter.getItemAtPos(position), itemsShop);
                    itemsBasket.remove(itemsBasket.indexOf(basketAdapter.getItemAtPos(position)));
                    basketAdapter.removeItemAnimated(view, position);
                    basketQuantity.setText("" + itemsBasket.size());
                    basketStatus.setText(String.format("%.2f",basketAdapter.getTotalCost())+" zł");
                    if (itemsBasket.isEmpty() && AnimationAlgorithms.getIfCollapsed(recViewBasket.getId()) == 0) {
                        AnimationAlgorithms.collapse(recViewBasket);
                        AnimationAlgorithms.collapse(basketButton);
                    }
                }
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<Item> filteredShopList = FilterAlgorithm.filter(itemsShop, charSequence);
                shopAdapter.animateTo(filteredShopList);
                recViewShop.scrollToPosition(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        AnimationAlgorithms.collapse(basketButton); AnimationAlgorithms.collapse(recViewBasket);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.test:
                if(itemsBasket.size() != 0){
                    Toast.makeText(MainActivity.this, "Nie można generować bazy gdy koszyk posiada produkty.", Toast.LENGTH_SHORT).show();
                }else if(iArratGeneerator != null && iArratGeneerator.getStatus() != AsyncTask.Status.FINISHED){
                    Toast.makeText(MainActivity.this, "Baza w trakcie generowania.", Toast.LENGTH_SHORT).show();
                }else if(DatabaseService.getIfRunning()){
                    Toast.makeText(MainActivity.this, "Baza w trakcie zapisywania.", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(MainActivity.this, "Rozpoczęto generowanie bazy.", Toast.LENGTH_SHORT).show();
                    generateDatabase();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DatabaseService.NOTIFICATION));
    }
    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.basket_button:
                if(AnimationAlgorithms.getIfCollapsed(recViewBasket.getId()) == 1) AnimationAlgorithms.expand(recViewBasket);
                else if(AnimationAlgorithms.getIfCollapsed(recViewBasket.getId())==0) AnimationAlgorithms.collapse(recViewBasket);
                break;
            default:
                break;
        }
    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector =  new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e){
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e){
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child!=null && clickListener != null){
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e){
            View child = rv.findChildViewUnder(e.getX(),e.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e){
            //nothing
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){
            //nothing
        }
    }

    private void generateDatabase(){
        itemsShop.clear();
        //new generateItems(this).execute("");
        iArratGeneerator = new ItemArrayGenerator(this, shopAdapter, itemsShop,1000);
        iArratGeneerator.execute("");
    }
}


