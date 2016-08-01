package com.byku.android.kamstore.RecView;


public class Item implements Comparable<Item>{
    private final String name;
    private String desc;
    private double cost;
    public Item(){
        name = new String("Default");
    }

    public Item(String name, String desc, double cost){
        if(name == null || desc == null)
            throw new NullPointerException();
        this.name = name;
        this.desc = desc;
        this.cost = cost;
    }
    public String getName(){return name;}
    public String getDesc(){return desc;}
    public double getCost(){return cost;}
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
}