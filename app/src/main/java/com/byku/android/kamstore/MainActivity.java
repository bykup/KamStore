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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.byku.android.kamstore.Algorithms.*;
import com.byku.android.kamstore.RecView.*;
/**
 * - baza sqlite
 * - wyszukiwanie
 * - nagłowek koszyka - aktualizacja z ceną
 * - przechowywanie danych
 * - nie wrzucanie tych samych produktow
 * - synchronizacja przy dodawaniu produktow(jak mamy liste z pasujacymi produktami do frazy wyszukiwania)
 * - synchronizacja przy usuwaniu produktow(jak mamy liste z pasujacymi produktami do frazy wyszukiwania)
 */
public class MainActivity extends AppCompatActivity{
    private RecyclerView recViewShop;
    private RecyclerView recViewBasket;
    private RelativeLayout basketButton;
    private ShopAdapter shopAdapter;
    private BasketAdapter basketAdapter;
    private EditText inputSearch;
    private TextView basketStatus;
    private TextView basketQuantity;
    private ArrayList<Item> itemsShop = new ArrayList<>();
    private ArrayList<Item> itemsBasket = new ArrayList<>();

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

        shopAdapter = new ShopAdapter(this, itemsShop);
        recViewShop.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recViewShop.setItemAnimator(new DefaultItemAnimator());
        recViewShop.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recViewShop.setAdapter(shopAdapter);
        recViewShop.setHasFixedSize(true);
        prepareItemData();

        basketAdapter = new BasketAdapter(this, itemsBasket);
        recViewBasket.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recViewBasket.setItemAnimator(new DefaultItemAnimator());
        recViewBasket.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recViewBasket.setAdapter(basketAdapter);
        recViewBasket.setHasFixedSize(true);

        recViewShop.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewShop, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                basketAdapter.addItemSorted(shopAdapter.getItemAtPos(position),MainActivity.this,itemsBasket);
                itemsShop.remove(itemsShop.indexOf(shopAdapter.getItemAtPos(position)));
                shopAdapter.removeItem(position);
                basketQuantity.setText("" + itemsBasket.size());
                basketStatus.setText(""+ basketAdapter.getTotalCost() + " zł");
                if(AnimationAlgorithms.getIfCollapsed(basketButton.getId()) == 1) AnimationAlgorithms.expand(basketButton);//*/
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        basketAdapter.setOnItemClickListener(new BasketAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position){
                shopAdapter.addItemSorted(basketAdapter.getItemAtPos(position),MainActivity.this,itemsShop);
                itemsBasket.remove(itemsBasket.indexOf(basketAdapter.getItemAtPos(position)));
                basketAdapter.removeItem(position);
                basketQuantity.setText("" + itemsBasket.size());
                basketStatus.setText(""+ basketAdapter.getTotalCost() + " zł");
                if(itemsBasket.isEmpty() && AnimationAlgorithms.getIfCollapsed(recViewBasket.getId())==0){
                    AnimationAlgorithms.collapse(recViewBasket);
                    AnimationAlgorithms.collapse(basketButton);
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
                shopAdapter.addItemSorted(new Item("ZG","test",222),MainActivity.this,itemsShop);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private class SortAndPublish extends AsyncTask<String, String, String> {
        ShopAdapter adapter;
        ArrayList<Item> list;

        SortAndPublish(ShopAdapter adapter, ArrayList<Item> list){
            this.adapter = adapter;
            this.list = list;

        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(String... args) {
            Log.i("LOG","doInBackground");
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i("LOG","onPostExecute");

        }
    }

    private void prepareItemData() {
        Item item = new Item("Mad Max: Fury Road", "Action & Adventure", 2015);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Inside Out", "Animation, Kids & Family", 2015);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Star Wars: Episode VII - The Force Awakens", "Action", 2015);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Shaun the Sheep", "Animation", 2015);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("The Martian", "Science Fiction & Fantasy", 2015);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Mission: Impossible Rogue Nation", "Action", 2015);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Up", "Animation", 2009);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Star Trek", "Science Fiction", 2009);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("The LEGO Item", "Animation", 2014);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Iron Man", "Action & Adventure", 2008);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Aliens", "Science Fiction", 198);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Chicken Run", "Animation", 2000);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Back to the Future", "Science Fiction", 1985);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Raiders of the Lost Ark", "Action & Adventure", 1981);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Goldfinger", "Action & Adventure", 1965);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);

        item = new Item("Guardians of the Galaxy", "Science Fiction & Fantasy", 2014);
        shopAdapter.addItemSorted(item,MainActivity.this,itemsShop);
    }

}
