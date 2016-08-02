package com.byku.android.kamstore.recview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byku.android.kamstore.R;
import com.byku.android.kamstore.algorithms.AnimationAlgorithms;
import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.MyViewHolder>{
    private final LayoutInflater itemInfalter;
    private ArrayList<Item> itemsList;
    private Context context;
    private boolean ifRemoving = false;
    /**
     * ifRemoving - protects the base from removing more than one item at a time, used in:
     * {@link #removeItemAnimated(View, int)}
     */
    private OnItemClickListener listener;
    /**
     * OnItemClickListener used to detect click on del(TextView)
     */
    public interface OnItemClickListener { void onItemClick(View itemView, int position); }
    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, desc, cost;
        public RelativeLayout relativeLayout;
        public TextView del;
        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.basket_name);
            desc = (TextView) view.findViewById(R.id.basket_desc);
            cost = (TextView) view.findViewById(R.id.basket_price);
            del = (TextView) view.findViewById(R.id.basket_delete);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.in_basket_list);

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        AnimationAlgorithms.setAnimationAddition(holder.relativeLayout,context);
    }

    @Override
    public void onViewDetachedFromWindow(final BasketAdapter.MyViewHolder holder){ holder.relativeLayout.clearAnimation(); }

    public BasketAdapter(Context context, ArrayList<Item> itemsList){
        itemInfalter = LayoutInflater.from(context);
        this.itemsList = new ArrayList<Item>(itemsList);
        this.context = context;
    }

    @Override
    public int getItemCount(){ return itemsList.size(); }
    public Item getItemAtPos(int position){ return itemsList.get(position); }
    public double getTotalCost(){
        double totalCost = 0.0;
        for(Item item : itemsList){
            totalCost += item.getCost();
        }
        return totalCost;
    }
    public ArrayList<Item> getListCopy(){ return new ArrayList<Item>(itemsList); }
    public boolean getIfRemoving(){ return ifRemoving; }

    public void setItemList(ArrayList<Item> itemsList) {
        this.itemsList = new ArrayList<Item>(itemsList);
        notifyDataSetChanged();
    }
    public void setIfRemoving(boolean irem){ this.ifRemoving = irem; }

    public void animateTo(ArrayList<Item> items){
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);
    }
    private void applyAndAnimateAdditions(ArrayList<Item> items) {
        for (int i = 0, count = items.size(); i < count; i++) {
            final Item item = items.get(i);
            if (!itemsList.contains(item)) {
                addItem(i, item);
            }
        }
    }
    private void applyAndAnimateRemovals(ArrayList<Item> items) {
        for (int i = itemsList.size() - 1; i >= 0; i--) {
            final Item item = itemsList.get(i);
            if (!items.contains(item)) {
                removeItem(i);
            }
        }
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

    public Item removeItem(int position) {
        final Item item = itemsList.remove(position);
        notifyItemRemoved(position);
        return item;
    }
    public void removeItemAnimated(final View view, final int position) {
            AnimationAlgorithms.setAnimationRemoval(view, context).setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animation.setFillAfter(true);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    itemsList.remove(position);
                    notifyItemRemoved(position);
                    view.setClickable(true);
                    ifRemoving = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
    }
    public int addItemSorted(Item item ,ArrayList<Item> sourceArray){
        int i = 0,j=0, itemsSourceSize = sourceArray.size(), itemsListSize = itemsList.size();
        if(sourceArray.contains(item)){
            Toast.makeText(context, "Produkt już w sklepie", Toast.LENGTH_SHORT).show();
            return -2;
        }
        while(j <= itemsSourceSize){
            if(j != itemsSourceSize && sourceArray.get(j).compareTo(item) > 0) {
                sourceArray.add(j, item);
                break;
            } else if(j==itemsSourceSize){
                sourceArray.add(item);
                break;
            }
            j++;
        }
        while(i <= itemsListSize){
            if(i != itemsListSize && itemsList.get(i).compareTo(item) > 0) {
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
    private void moveItem(int fromPosition, int toPosition) {
        final Item item = itemsList.remove(fromPosition);
        itemsList.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }
}