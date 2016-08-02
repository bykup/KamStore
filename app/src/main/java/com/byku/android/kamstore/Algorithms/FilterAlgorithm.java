package com.byku.android.kamstore.algorithms;

import com.byku.android.kamstore.recview.Item;

import java.util.ArrayList;

public class FilterAlgorithm {
    private FilterAlgorithm(){}

    public static ArrayList<Item> filter(ArrayList<Item> list,CharSequence cs){
        cs = cs.toString().toLowerCase();
        ArrayList<Item> filteredItemList = new ArrayList<>();
        for (Item item : list) {
            String text = item.getName().toLowerCase();
            if (text.contains(cs)) {
                filteredItemList.add(item);
            }
        }
        return filteredItemList;
    }

}
