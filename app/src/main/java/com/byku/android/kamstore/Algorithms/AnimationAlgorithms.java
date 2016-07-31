package com.byku.android.kamstore.Algorithms;

import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;


/**
 * Created by Byku on 28.07.2016.
 */
final public class AnimationAlgorithms {
    private AnimationAlgorithms(){}
    private static SparseIntArray ifCollapsed = new SparseIntArray();
    private static SparseIntArray viewsDimensions = new SparseIntArray();


    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //final int targetHeight = v.getMeasuredHeight();
        final int targetHeight = viewsDimensions.get(v.getId())<1 ? 1 : viewsDimensions.get(v.getId());
        //Log.i("LOG","Wys expand " + targetHeight);
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = targetHeight == 1 ? -2 : (int)(targetHeight * interpolatedTime);
                //Log.i("LOG","height: " + v.getLayoutParams().height + ", float" + interpolatedTime);
                v.requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        //stores states of views
        ifCollapsed.put(v.getId(),0);
        // 1dp/ms
        a.setDuration((int)(targetHeight*3 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        viewsDimensions.put(v.getId(),v.getLayoutParams().height);
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };


        //stores states of views
        ifCollapsed.put(v.getId(),1);
        // 1dp/ms
        a.setDuration((int)(initialHeight*3 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    //implement exceptions!!
    public static Integer getIfCollapsed(Integer id){
        return ifCollapsed.get(id);
    }
}
