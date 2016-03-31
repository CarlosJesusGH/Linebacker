package com.cmsys.linebacker.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by @CarlosJesusGH on 29/03/16.
 */
public class IncomingCallAnimation extends Animation {
    final int targetHeight;
    View view;
    int startHeight;

    public IncomingCallAnimation(View view, int targetHeight, int startHeight) {
        this.view = view;
//        this.targetHeight = targetHeight;
//        this.startHeight = startHeight;
        // Get values in pixels using the display scale factor
        final float scale = view.getContext().getResources().getDisplayMetrics().density;
        this.targetHeight = (int) (targetHeight * scale + 0.5f);
        this.startHeight = (int) (startHeight * scale + 0.5f);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = (int) (startHeight + targetHeight * interpolatedTime);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}