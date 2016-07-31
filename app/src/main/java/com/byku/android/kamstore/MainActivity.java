package com.byku.android.kamstore;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;

import com.byku.android.kamstore.Algorithms.*;
import com.byku.android.kamstore.RecView.*;
/**
 * - baza sqlite
 * - wyszukiwanie
 * - nagłowek koszyka - aktualizacja z ceną
 * - przechowywanie danych
 */
public class MainActivity extends AppCompatActivity implements BasketQuantity{
    private RecyclerView recViewShop;
    private RecyclerView recViewBasket;
    private Button basketButton;
    private ItemAdapter shopAdapter;
    private ItemAdapter basketAdapter;
    private EditText inputSearch;
    ArrayList<Item> itemsShop = new ArrayList<>();
    ArrayList<Item> itemsBasket = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recViewShop = (RecyclerView) findViewById(R.id.store_list);
        recViewBasket = (RecyclerView) findViewById(R.id.basket_list);
        basketButton = (Button) findViewById(R.id.basket_button);
        inputSearch = (EditText) findViewById(R.id.search_bar);


        shopAdapter = new ItemAdapter(this, itemsShop);
        recViewShop.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recViewShop.setItemAnimator(new DefaultItemAnimator());
        recViewShop.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recViewShop.setAdapter(shopAdapter);
        prepareItemData();
        

        basketAdapter = new ItemAdapter(this, itemsBasket);
        recViewBasket.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recViewBasket.setItemAnimator(new DefaultItemAnimator());
        recViewBasket.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recViewBasket.setAdapter(basketAdapter);



        recViewShop.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewShop, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item item = itemsShop.get(position);
                itemsBasket.add(item);
                itemsShop.remove(position);
                shopAdapter.notifyDataSetChanged();
                new SortAndPublish(basketAdapter,itemsBasket,MainActivity.this).execute("");
                if(AnimationAlgorithms.getIfCollapsed(basketButton.getId()) == 1) AnimationAlgorithms.expand(basketButton);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recViewBasket.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewShop, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item item = itemsBasket.get(position);
                itemsShop.add(item);
                itemsBasket.remove(position);
                basketAdapter.notifyDataSetChanged();
                new SortAndPublish(shopAdapter,itemsShop,MainActivity.this).execute("");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));//*/

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final ArrayList<Item> filteredShopList = FilterAlgorithm.filter(itemsShop, charSequence);
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
                //Log.i("LOG:", "KLAWISZ: " + shopAdapter.getCount());
                //improve detection of suspicious keys
                //if(TestingAlgorithms.getIfCollapsed(item.getItemId()) == 0) TestingAlgorithms.expand(basketButton);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.basket_button:
                Log.i("LOG","ifCollapsed " + AnimationAlgorithms.getIfCollapsed(view.getId()));
                if(AnimationAlgorithms.getIfCollapsed(recViewBasket.getId()) == 1) AnimationAlgorithms.expand(recViewBasket);
                else if(AnimationAlgorithms.getIfCollapsed(recViewBasket.getId())==0) AnimationAlgorithms.collapse(recViewBasket);
                else Log.i("LOG","Error");
                break;
            default:
                Log.i("LOG","Button not programmed");
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

    private class SortAndPublish extends AsyncTask<String, String, String> {
        ItemAdapter adapter;
        ArrayList<Item> list;
        BasketQuantity bq;

        SortAndPublish(ItemAdapter adapter, ArrayList<Item> list,BasketQuantity bq){
            this.adapter = adapter;
            this.list = list;
            this.bq = bq;
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(String... args) {
            Log.i("LOG","doInBackground");
            Collections.sort(list);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i("LOG","onPostExecute");
            bq.checkBasket();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void checkBasket(){
        if(itemsBasket.isEmpty() && AnimationAlgorithms.getIfCollapsed(recViewBasket.getId())==0){
            AnimationAlgorithms.collapse(recViewBasket);
            AnimationAlgorithms.collapse(basketButton);
        }
    }


    private void prepareItemData() {
        Item item = new Item("Mad Max: Fury Road", "Action & Adventure", "2015");
        itemsShop.add(item);

        item = new Item("Inside Out", "Animation, Kids & Family", "2015");
        itemsShop.add(item);

        item = new Item("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        itemsShop.add(item);

        item = new Item("Shaun the Sheep", "Animation", "2015");
        itemsShop.add(item);

        item = new Item("The Martian", "Science Fiction & Fantasy", "2015");
        itemsShop.add(item);

        item = new Item("Mission: Impossible Rogue Nation", "Action", "2015");
        itemsShop.add(item);

        item = new Item("Up", "Animation", "2009");
        itemsShop.add(item);

        item = new Item("Star Trek", "Science Fiction", "2009");
        itemsShop.add(item);

        item = new Item("The LEGO Item", "Animation", "2014");
        itemsShop.add(item);

        item = new Item("Iron Man", "Action & Adventure", "2008");
        itemsShop.add(item);

        item = new Item("Aliens", "Science Fiction", "1986");
        itemsShop.add(item);

        item = new Item("Chicken Run", "Animation", "2000");
        itemsShop.add(item);

        item = new Item("Back to the Future", "Science Fiction", "1985");
        itemsShop.add(item);

        item = new Item("Raiders of the Lost Ark", "Action & Adventure", "1981");
        itemsShop.add(item);

        item = new Item("Goldfinger", "Action & Adventure", "1965");
        itemsShop.add(item);

        item = new Item("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        itemsShop.add(item);
        new SortAndPublish(shopAdapter,itemsShop,MainActivity.this).execute("");
    }

}
