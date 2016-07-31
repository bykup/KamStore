package com.byku.android.kamstore.RecView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byku.android.kamstore.R;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder>{
    private final LayoutInflater itemInfalter;
    private ArrayList<Item> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, desc, cost;
        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            desc = (TextView) view.findViewById(R.id.item_desc);
            cost = (TextView) view.findViewById(R.id.item_price);
        }
    }

    public ItemAdapter(Context context, ArrayList<Item> itemList){
        itemInfalter = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final View itemView = itemInfalter.inflate(R.layout.shop_items,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Item item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDesc());
        holder.cost.setText(item.getCost());
    }

    @Override
    public int getItemCount(){
        return itemList.size();
    }
    //sprawdzic poprawne usuwanie plikow
    public void animateTo(ArrayList<Item> items){
        /*applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);//*/
        itemList = items;
        notifyDataSetChanged();
    }

    private void applyAndAnimateRemovals(ArrayList<Item> items) {
        for (int i = itemList.size() - 1; i >= 0; i--) {
            final Item item = itemList.get(i);
            if (!items.contains(item)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Item> items) {
        for (int i = 0, count = items.size(); i < count; i++) {
            final Item item = items.get(i);
            if (!itemList.contains(item)) {
                addItem(i, item);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Item> items) {
        for (int toPosition = items.size() - 1; toPosition >= 0; toPosition--) {
            final Item item = items.get(toPosition);
            final int fromPosition = itemList.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void setItemList(ArrayList<Item> itemList){
        this.itemList = new ArrayList<Item>(itemList);
    }

    public Item removeItem(int position) {
        final Item item = itemList.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void addItem(int position, Item item) {
        itemList.add(position, item);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Item item = itemList.remove(fromPosition);
        itemList.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }
}
