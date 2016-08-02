package com.byku.android.kamstore.recview.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byku.android.kamstore.R;
import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.MyViewHolder>{
    private final LayoutInflater itemInfalter;
    private ArrayList<Item> itemsList;
    private Context context;

    private static OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, desc, cost;
        public TextView del;
        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.basket_name);
            desc = (TextView) view.findViewById(R.id.basket_desc);
            cost = (TextView) view.findViewById(R.id.basket_price);
            del = (TextView) view.findViewById(R.id.basket_delete);

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final View itemView = itemInfalter.inflate(R.layout.basket_items,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Item item = itemsList.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDesc());
        holder.cost.setText(String.format("%.2f",item.getCost())+" zł");

    }

    public BasketAdapter(Context context, ArrayList<Item> itemsList){
        itemInfalter = LayoutInflater.from(context);
        this.itemsList = new ArrayList<Item>(itemsList);
        this.context = context;
    }

    @Override
    public int getItemCount(){
        return itemsList.size();
    }

    public Item getItemAtPos(int position){
        return itemsList.get(position);
    }

    public void animateTo(ArrayList<Item> items){
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);
    }
    private void applyAndAnimateRemovals(ArrayList<Item> items) {
        for (int i = itemsList.size() - 1; i >= 0; i--) {
            final Item item = itemsList.get(i);
            if (!items.contains(item)) {
                removeItem(i);
            }
        }
    }
    public Item removeItem(int position) {
        final Item item = itemsList.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    private void applyAndAnimateAdditions(ArrayList<Item> items) {
        for (int i = 0, count = items.size(); i < count; i++) {
            final Item item = items.get(i);
            if (!itemsList.contains(item)) {
                addItem(i, item);
            }
        }
    }
    public int addItemSorted(Item item, Activity mainActivity,ArrayList<Item> sourceArray){
        int i = 0,j=0, itemsSourceSize = sourceArray.size(), itemsListSize = itemsList.size();
        if(sourceArray.contains(item)){
            Toast.makeText(mainActivity, "Produkt już w sklepie", Toast.LENGTH_SHORT).show();
            return -2;
        }
        while(j <= itemsSourceSize){
            if(j != itemsSourceSize && sourceArray.get(j).compareTo(item) > 0) {
                //Log.i("Log:","addSourceArray if " + item + " " + j);
                sourceArray.add(j, item);
                break;
            } else if(j==itemsSourceSize){
                //Log.i("Log:","addSourceArray if else " + item + " " + j);
                sourceArray.add(item);
                break;
            }
            j++;
        }
        while(i <= itemsListSize){
            if(i != itemsListSize && itemsList.get(i).compareTo(item) > 0) {
                //Log.i("Log:","addItemSorted " + itemsList.get(i) + " " + item + " " + itemsList.get(i).compareTo(item));
                itemsList.add(i, item);
                notifyItemInserted(i);
                return i;
            }else if(i==itemsListSize) {
                itemsList.add(item);
                notifyItemInserted(i);
                return i;
            }
            i++;
        }
        return -1;
    }
    private void addItem(int position, Item item) {
        itemsList.add(position, item);
        notifyItemInserted(position);
    }

    private void applyAndAnimateMovedItems(ArrayList<Item> items) {
        for (int toPosition = items.size() - 1; toPosition >= 0; toPosition--) {
            final Item item = items.get(toPosition);
            final int fromPosition = itemsList.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    public void moveItem(int fromPosition, int toPosition) {
        final Item item = itemsList.remove(fromPosition);
        itemsList.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setItemList(ArrayList<Item> itemsList) {
        this.itemsList = new ArrayList<Item>(itemsList);
        notifyDataSetChanged();
    }

    public double getTotalCost(){
        double totalCost = 0.0;
        for(Item item : itemsList){
            totalCost += item.getCost();
        }
        return totalCost;
    }

    public ArrayList<Item> getListCopy(){
        return new ArrayList<Item>(itemsList);
    }

}
