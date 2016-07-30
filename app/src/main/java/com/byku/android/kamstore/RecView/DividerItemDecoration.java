package com.byku.android.kamstore.RecView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration{
    //int array named ATTRS that has ints from "listDivider" - which is a drawable for
    //listDivider - drawable for list divider, it's a const value 16843284
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    //Const value = 0
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    //Const value = 1
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable mDivider; //Drawable - a general abstraction of something that can be drawn
    private int mOrientation; //well orientation duh

    //constructor(interface to global information, orientation of screen
    public DividerItemDecoration(Context context, int orientation){
        Log.i("Log: ","DividerItemDecoration: In constructor.");
        //obtainStyledAttributes - returns TypedArray - holding the values defined
        // by Theme which are listed in attrs.
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0); //gets drawable from the attr at index 0
        a.recycle(); //has to be used
        setOrientation(orientation); //sets orientation
    }

    public void setOrientation(int orientation){
        Log.i("Log: ","DividerItemDecoration: In setOrientation.");
        if(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
        //c 	Canvas: Canvas to draw into
        //parent 	RecyclerView: RecyclerView this ItemDecoration is drawing into
        //state 	RecyclerView.State: The current state of RecyclerView.
        Log.i("Log: ","DividerItemDecoration: In onDrawOver.");
        if(mOrientation==VERTICAL_LIST){
            drawVertical(c,parent);
        }else{
            drawHorizontal(c,parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent){
        Log.i("Log: ","DividerItemDecoration: In drawVertiacal.");
        final int left = parent.getPaddingLeft();
        Log.i("Log: ","DividerItemDecoration: Left padding: " + Integer.toString(left));
        final int right = parent.getWidth() - parent.getPaddingRight();
        Log.i("Log: ","DividerItemDecoration: Right padding: " + Integer.toString(right) + " so, parent.width:" + Integer.toString(parent.getWidth()) + " - " + Integer.toString(parent.getPaddingRight()));
        final int childCount = parent.getChildCount(); //returns amount of VISIBLE children
        Log.i("Log: ","DividerItemDecoration: childCount: " + Integer.toString(childCount));
        for(int i = 0; i< childCount;i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            Log.i("Log: ","DividerItemDecoration: top:" + Integer.toString(top)+", bottom: " + Integer.toString(bottom));
            //setBounds - sets bounds for a square it draws
            mDivider.setBounds(left,top,right,bottom);
            //draws that square
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent){
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for(int i = 0; i< childCount; i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }

    @Override
    /*Retrieve any offsets for the given item. Each field of outRect specifies the number of pixels
    that the item view should be inset by, similar to padding or margin. The default implementation
    sets the bounds of outRect to 0 and returns.
    If this ItemDecoration does not affect the positioning of item views, it should set all
    four fields of outRect (left, top, right, bottom) to zero before returning. */
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        Log.i("Log: ","DividerItemDecoration: In getItemOffsets.");
        if(mOrientation==VERTICAL_LIST){
            Log.i("Log: ","DividerItemDecoration: getIntrisinsicHeight: " + Integer.toString(mDivider.getIntrinsicHeight()));
            //getIntrisicHeight - get's "real" height of mDivider
            outRect.set(0,0,0, mDivider.getIntrinsicHeight());
        }else{
            outRect.set(0,0,mDivider.getIntrinsicWidth(),0);
        }
    }
}
