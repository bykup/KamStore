package com.byku.android.kamstore.algorithms;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

public class AnimationAlgorithms {
    private AnimationAlgorithms(){}
    private static SparseIntArray viewsCollapsed = new SparseIntArray();
    private static SparseIntArray viewsDimensions = new SparseIntArray();

    public static void expand(final View viewToExpand) {
        viewToExpand.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = viewsDimensions.get(viewToExpand.getId())<1 ? 1 : viewsDimensions.get(viewToExpand.getId());
        viewToExpand.getLayoutParams().height = 1;
        viewToExpand.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                viewToExpand.getLayoutParams().height = targetHeight == 1 ? -2 : (int)(targetHeight * interpolatedTime);
                viewToExpand.requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        viewsCollapsed.put(viewToExpand.getId(),0);
        a.setDuration((int)(targetHeight*3 / viewToExpand.getContext().getResources().getDisplayMetrics().density));
        viewToExpand.startAnimation(a);
    }

    public static void collapse(final View viewToCollapse) {
        viewsDimensions.put(viewToCollapse.getId(),viewToCollapse.getLayoutParams().height);
        final int initialHeight = viewToCollapse.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    viewToCollapse.setVisibility(View.GONE);
                }else{
                    viewToCollapse.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    viewToCollapse.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };


        viewsCollapsed.put(viewToCollapse.getId(),1);
        a.setDuration((int)(initialHeight*3 / viewToCollapse.getContext().getResources().getDisplayMetrics().density));
        viewToCollapse.startAnimation(a);
    }

    public static Animation setAnimationRemoval(View viewToAnimate, Context context){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        viewToAnimate.startAnimation(animation);
        return animation;
    }

    public static Animation setAnimationAddition(View viewToAnimate, Context context){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
        return animation;
    }

    public static Integer getIfCollapsed(Integer viewId){
        return viewsCollapsed.get(viewId);
    }
}
