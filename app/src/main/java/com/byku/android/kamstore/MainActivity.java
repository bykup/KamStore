package com.byku.android.kamstore;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import com.byku.android.kamstore.Algorithms.*;
/**
 * - baza sqlite
 * - wyszukiwanie
 * - nagłowek koszyka - aktualizacja z ceną
 * - przechowywanie danych
 */
public class MainActivity extends AppCompatActivity implements BasketQuantity{
    private ListView listViewShop;
    private ListView listViewBasket;
    private Button basketButton;
    private ArrayAdapter shopAdapter;
    private ArrayAdapter basketAdapter;
    private EditText editText;
    ArrayList<String> valuesShop;
    ArrayList<String> valuesBasket;


    String[] strings = new String[]{"a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "w", "x", "y", "z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewShop = (ListView) findViewById(R.id.store_list);
        listViewBasket = (ListView) findViewById(R.id.basket_list);
        basketButton = (Button) findViewById(R.id.basket_button);
        editText = (EditText) findViewById(R.id.search_bar);

        valuesShop = new ArrayList<String>();
        for(String a : strings){
            valuesShop.add(a);
        }//*//*
        valuesBasket = new ArrayList<String>();

        shopAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,valuesShop);
        listViewShop.setDividerHeight(2);
        listViewShop.setAdapter(shopAdapter);
        basketAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,valuesBasket);
        listViewBasket.setDividerHeight(2);
        listViewBasket.setAdapter(basketAdapter);

        listViewShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = (String)adapterView.getItemAtPosition(i);
                valuesBasket.add(temp);
                valuesShop.remove(i);
                shopAdapter.notifyDataSetChanged();
                new SortAndPublish(basketAdapter,valuesBasket,MainActivity.this).execute("");
                if(TestingAlgorithms.getIfCollapsed(basketButton.getId()) == 1) TestingAlgorithms.expand(basketButton);
            }
        });
        listViewBasket.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                String temp = (String)adapterView.getItemAtPosition(i);
                valuesShop.add(temp);
                valuesBasket.remove(i);
                basketAdapter.notifyDataSetChanged();
                new SortAndPublish(shopAdapter,valuesShop,MainActivity.this).execute("");
            }
        });//*/

        TestingAlgorithms.collapse(basketButton); TestingAlgorithms.collapse(listViewBasket);
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
                Log.i("LOG:", "KLAWISZ: " + shopAdapter.getCount());
                //improve detection of suspicious keys
                //if(TestingAlgorithms.getIfCollapsed(item.getItemId()) == 0) TestingAlgorithms.expand(basketButton);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.basket_button:
                Log.i("LOG","ifCollapsed " + TestingAlgorithms.getIfCollapsed(view.getId()));
                if(TestingAlgorithms.getIfCollapsed(listViewBasket.getId()) == 1) TestingAlgorithms.expand(listViewBasket);
                else if(TestingAlgorithms.getIfCollapsed(listViewBasket.getId())==0) TestingAlgorithms.collapse(listViewBasket);
                else Log.i("LOG","Error");
                break;
            default:
                Log.i("LOG","Button not programmed");
        }
    }

    //@todo zwiększyć ilość argumentów konstruktorze lub zaimplementować callback
    private class SortAndPublish extends AsyncTask<String, String, String> {
        //Mozna tu wkleic animacje! :D No moze nie tu, ale gdzies
        ArrayAdapter<String> adapter;
        ArrayList<String> list;
        BasketQuantity bq;

        SortAndPublish(ArrayAdapter<String> adapter, ArrayList<String> list,BasketQuantity bq){
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
        if(valuesBasket.isEmpty() && TestingAlgorithms.getIfCollapsed(listViewBasket.getId())==0){
            TestingAlgorithms.collapse(listViewBasket);
            TestingAlgorithms.collapse(basketButton);
        }
    }
}
