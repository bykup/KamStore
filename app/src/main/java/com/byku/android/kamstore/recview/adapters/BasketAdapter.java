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

public class BasketAdapter extends ItemAdapter{

    public BasketAdapter(Context context, ArrayList<Item> itemsList){ super(context, itemsList); }

    public class BkHolder extends MyViewHolder{
        public TextView name, desc, cost;
        public RelativeLayout relativeLayout;
        public TextView del;
        public BkHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.basket_name);
            desc = (TextView) view.findViewById(R.id.basket_desc);
            cost = (TextView) view.findViewById(R.id.basket_price);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.in_basket_list);
            del = (TextView) view.findViewById(R.id.basket_delete);

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
        final View itemView = itemInflater.inflate(R.layout.basket_items,parent,false);
        return new BkHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Item item = itemsList.get(position);
        ((BkHolder)holder).name.setText(item.getName());
        ((BkHolder)holder).desc.setText(item.getDesc());
        ((BkHolder)holder).cost.setText(String.format("%.2f",item.getCost())+" zł");
        AnimationAlgorithms.setAnimationAddition(((BkHolder)holder).relativeLayout,context);
    }

    @Override
    public void onViewDetachedFromWindow(final ItemAdapter.MyViewHolder holder){
        ((BkHolder)holder).relativeLayout.clearAnimation();
    }
}
