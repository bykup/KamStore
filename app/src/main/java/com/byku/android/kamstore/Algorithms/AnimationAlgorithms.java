package com.byku.android.kamstore.algorithms;

import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationAlgorithms {
    private AnimationAlgorithms(){}
    private static SparseIntArray ifCollapsed = new SparseIntArray();
    private static SparseIntArray viewsDimensions = new SparseIntArray();

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = viewsDimensions.get(v.getId())<1 ? 1 : viewsDimensions.get(v.getId());
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = targetHeight == 1 ? -2 : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        ifCollapsed.put(v.getId(),0);
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


        ifCollapsed.put(v.getId(),1);
        a.setDuration((int)(initialHeight*3 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static Integer getIfCollapsed(Integer id){
        return ifCollapsed.get(id);
    }
}
