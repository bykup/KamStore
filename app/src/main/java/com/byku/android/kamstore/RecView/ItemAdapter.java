package com.byku.android.kamstore.RecView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byku.android.kamstore.R;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder>{
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

    public ItemAdapter(ArrayList<Item> itemList){
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_items,parent,false);

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
}
