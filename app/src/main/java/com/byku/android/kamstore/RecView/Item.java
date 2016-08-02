package com.byku.android.kamstore.recview;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Comparable<Item>,Parcelable{
    private long id;
    private String name;
    private String desc;
    private double cost;

    public Item(String name, String desc, double cost){
        if(name == null || desc == null)
            throw new NullPointerException();
        this.name = name;
        this.desc = desc;
        this.cost = cost;
    }
    public Item(Long id, String name, String desc, double cost){
        if(name == null || desc == null)
            throw new NullPointerException();
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.cost = cost;
    }
    public Item(Long id, Item item){
        this.id = id;
        this.name = item.getName();
        this.desc = item.getDesc();
        this.cost = item.getCost();
    }
    public Item(Parcel in){
        this.id = in.readLong();
        this.name = in.readString();
        this.desc = in.readString();
        this.cost = in.readDouble();
    }

    public String getName(){return name;}
    public String getDesc(){return desc;}
    public double getCost(){return cost;}
    public long getId(){return id;}
    public void setDesc(String desc){this.desc = desc;}
    public void setCost(double cost){this.cost = cost;}


    @Override
    public boolean equals(Object object){
        if(!(object instanceof Item))
            return false;
        Item item = (Item) object;
        return item.name.equals(this.name);
    }
    @Override
    public int hashCode(){
        return 31*name.hashCode();
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Item i){
        int lastCmp = name.compareTo(i.getName());
        return (lastCmp !=0 ? lastCmp : name.compareTo(i.getName()));
    }

    @Override
    public int describeContents(){ return 0; }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeDouble(this.cost);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        public Item createFromParcel(Parcel in){
            return new Item(in);
        }
        public Item[] newArray(int size){
            return new Item[size];
        }
    };
}
