package com.byku.android.kamstore.recview.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byku.android.kamstore.R;
import com.byku.android.kamstore.algorithms.AnimationAlgorithms;
import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

/**
 * Created by Byku on 05.08.2016.
 */
public class ShAdapter extends ItemAdapter {

    public ShAdapter(Context context, ArrayList<Item> itemsList){
        super(context,itemsList);
    }

    public class ShHolder extends MyViewHolder{
        public TextView name, desc, cost;
        public RelativeLayout relativeLayout;
        public ShHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.shop_name);
            desc = (TextView) view.findViewById(R.id.shop_desc);
            cost = (TextView) view.findViewById(R.id.shop_price);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.in_store_list);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final View itemView = itemInflater.inflate(R.layout.shop_items,parent,false);
        return new ShHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Item item = itemsList.get(position);
        ((ShHolder)holder).name.setText(item.getName());
        ((ShHolder)holder).desc.setText(item.getDesc());
        ((ShHolder)holder).cost.setText(String.format("%.2f",item.getCost())+" zł");
        AnimationAlgorithms.setAnimationAddition(((ShHolder)holder).relativeLayout,context);
    }

    @Override
    public void onViewDetachedFromWindow(final ItemAdapter.MyViewHolder holder){
        ((ShHolder)holder).relativeLayout.clearAnimation();
    }
}
