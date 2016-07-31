package com.byku.android.kamstore.Algorithms;

import com.byku.android.kamstore.RecView.Item;

import java.util.ArrayList;

/**
 * Created by Byku on 30.07.2016.
 */
public class FilterAlgorithm {
    private FilterAlgorithm(){};


    public static ArrayList<Item> filter(ArrayList<Item> list,CharSequence cs){
        cs = cs.toString().toLowerCase();

        final ArrayList<Item> filteredItemList = new ArrayList<>();
        for (Item item : list) {
            final String text = item.getName().toLowerCase();
            if (text.contains(cs)) {
                filteredItemList.add(item);
            }
        }
        return filteredItemList;
    }
}
